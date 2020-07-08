package dev.digytal.ymltest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class YmlTestCase {

	static final Logger LOGGER = LoggerFactory.getLogger(YmlTestCase.class);
	
	static final ObjectMapper YAML_MAPPER = YmlObjectMapperFactory.getYamlMapper();
	
	ObjectNode objectNode;
	
	public static class CollectionWrapper {
		public int size;
		public ArrayNode items;
	}
	
	static class Context {
		Object bean;
		Class<?> beanType;
		String fieldName;
		Object fieldValue;
		Class<?> fieldType;
		String fullFieldName;
		
		Context parent;
		
		Context(Object bean) {
			this.bean = bean;
			if (bean != null) {
				this.beanType = bean.getClass();
			}
		}
		
		Context(Object bean, String fieldName) {
			this(bean, fieldName, null);
		}
		
		Context(Object bean, String fieldName, Context parent) {
			this(bean);
			this.fieldName = fieldName;
			if (bean != null) {
				try {
					this.fieldValue = PropertyUtils.getSimpleProperty(bean, fieldName);
					this.fieldType = PropertyUtils.getPropertyType(bean, fieldName);
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					String message = String.format("Field '%s' not found in class '%s'", fieldName, beanType.getName());
					throw new IllegalStateException(message);
				}
			}
			this.parent = parent;
			this.fullFieldName = getFullFieldName();
		}
		
		String getFullFieldName() {
			if (fieldName == null) {
				return "";
			}
			if (fieldName.startsWith("[") && fieldName.endsWith("]")) {
				return parent.getFullFieldName() + fieldName;
			}
			if (parent == null || parent.fieldName == null) {
				return fieldName;
			} else {
				return parent.getFullFieldName() + "." + fieldName;
			}
		}

		boolean isNumber() {
			return Number.class.isAssignableFrom(fieldType);
		}

		boolean isCollection() {
			return Collection.class.isAssignableFrom(fieldType);
		}

	}
	
	public YmlTestCase(ObjectNode objectNode) {
		this.objectNode = objectNode;
	}
	
	public static YmlTestCase ofResource(String resource) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		try (InputStream input = classLoader.getResourceAsStream(resource)) {
			
			if (input == null) {
				throw new IllegalArgumentException("Resource not found: " + resource);
			}
			
			return ofStream(input);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static YmlTestCase ofStream(InputStream input) throws IOException {
		if (input == null) {
			throw new IllegalArgumentException("InputStream is null");
		}
		
		JsonNode jsonNode = YAML_MAPPER.readTree(input);
		if (jsonNode.isMissingNode()) {
			throw new IllegalStateException("Invalid Yaml TestCase");
		}
		ObjectNode node = (ObjectNode) jsonNode;
		YmlTestCase testCase = new YmlTestCase(node);
		
		return testCase;		
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getInput() {
		try {
			JsonNode inputNode = objectNode.get("INPUT");
			String className = inputNode.get("_class").asText();
			Class<T> clazz = (Class<T>) Class.forName(className);
			return YAML_MAPPER.treeToValue(inputNode, clazz);
		} catch (ClassNotFoundException | JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public JsonNode getOutputNode() {
		return objectNode.get("OUTPUT");
	}
	
	public ObjectNode getExceptionNode() {
		JsonNode jsonNode = objectNode.get("OUTPUT").get("_exception");
		if (jsonNode == null) {
			return null;
		}
		ObjectNode objNode;
		if (jsonNode.getNodeType() == JsonNodeType.STRING) {
			objNode = objectNode.objectNode();
			objNode.put("message", jsonNode.asText());
		} else {
			objNode = jsonNode.deepCopy();
			if (!objNode.has("_class")) {
				objNode.remove("type");
				objNode.put("_class", objNode.path("type").asText());
			}
		}
		return objNode;
	}
	
	public ArrayNode getContextData() {
		return (ArrayNode) objectNode.get("CONTEXT");
	}

	public <T> void assertOutput(T output) {
		JsonNode outputNode = objectNode.get("OUTPUT");
		
		if (outputNode.has("_exception")) {
			String exceptionName = getExceptionName();
			fail("Should throw exception: " + exceptionName);
		}
		
		Context context = new Context(output);
		
		assertObject(outputNode, output, context);
	}
	
	private String getExceptionName() {
		JsonNode node = objectNode.get("OUTPUT").path("_exception");
		
		if (node.getNodeType() == JsonNodeType.STRING) {
			return node.asText();
		}
		if (node.getNodeType() == JsonNodeType.OBJECT) {
			if (node.has("_class")) {
				return node.get("_class").asText();
			}
			return node.path("type").asText();
		}
		return null;
	}

	public <INPUT, OUTPUT> OUTPUT process(Function<INPUT, OUTPUT> processor) {
		try {
			INPUT input = getInput(); 
			OUTPUT output = processor.apply(input);
			assertOutput(output);
			return output;
		} catch (Exception e) {
			if (!assertException(e)) {
				throw e;
			}
			return null;
		}
	}
	
	private boolean assertException(Exception exception) {
		String typeName = getExceptionName();
		
		if (typeName == null) {
			return false;
		}
		try {
			Class<?> exceptionType = Class.forName(typeName);
			if (!exceptionType.isInstance(exception)) {
				String message = String.format(
					"Exception type is not equals ==> expected: <%s> but was: <%s>", 
					exceptionType.getName(), exception.getClass().getName()
				);
				Assertions.fail(message);
			}
			ObjectNode exceptionNode = getExceptionNode();
			
			Context context = new Context(null, "exception");
			assertObject(exceptionNode, exception, context);
			return true;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private void assertObject(JsonNode expected, Object actual, Context context) {
		expected.fieldNames().forEachRemaining(fieldName -> {
			if (fieldName.startsWith("_")) {
				return;
			}
			Context fieldContext = new Context(actual, fieldName, context);
			LOGGER.debug("Asserting field: {}", fieldContext.fullFieldName);
			assertField(expected, actual, fieldContext);
		});
	}
	
	private void assertField(JsonNode expected, Object actual, Context context) {
		String fieldName = context.fieldName;
		Object fieldValue = context.fieldValue;
		Class<?> fieldType = context.fieldType;
		
		JsonNode jsonValue = expected.get(fieldName);
		
		Object expectedValue = getRawValue(jsonValue, fieldType, context);
		
		String message = "Field not equals: " + context.fullFieldName;
		
		if (context.isNumber() && fieldValue != null) {
			BigDecimal actualNumber = fieldValue instanceof BigDecimal ? (BigDecimal) fieldValue : new BigDecimal(fieldValue.toString());
			BigDecimal expectedNumber = expectedValue instanceof BigDecimal ? (BigDecimal) expectedValue : new BigDecimal(expectedValue.toString());
			
			if (actualNumber.compareTo(expectedNumber) != 0) {
				message += String.format(" ==> expected: <%s> but was: <%s>", expectedNumber, actualNumber);
				Assertions.fail(message);
			}
		} else if (expectedValue instanceof CollectionWrapper) {
			CollectionWrapper cw = (CollectionWrapper)expectedValue;
			assertNotNull(fieldValue, "Field shold not be null: " + context.fullFieldName);
			Collection<?> collectionValue = (Collection<?>)fieldValue;
			assertCollection(cw, collectionValue, context);
		} else {
			assertEquals(expectedValue, fieldValue, message);
		}
	}

	private void assertCollection(CollectionWrapper expected, Collection<?> actual, Context context) {
		assertEquals(expected.size, actual.size(), "Collection size not equals: " + context.fullFieldName);
		
		Iterator<JsonNode> expectedIterator = expected.items.iterator();
		Iterator<?> actualIterator = actual.iterator();
		
		for (int i = 0; expectedIterator.hasNext() && actualIterator.hasNext(); i++) {
			JsonNode expectedItem = expectedIterator.next();
			Object actualItem = actualIterator.next();
			
			Context itemContext = new Context(actual);
			itemContext.beanType = context.fieldType;
			itemContext.fieldName = "[" + i + "]";
			itemContext.fieldType = actualItem.getClass();
			itemContext.fieldValue = actualItem;
			itemContext.parent = context;
			itemContext.fullFieldName = itemContext.getFullFieldName();
			
			assertObject(expectedItem, actualItem, itemContext);
		}
	}

	private Object getRawValue(JsonNode jsonValue, Class<?> type, Context context) {
		JsonNodeType nodeType = jsonValue.getNodeType();
		if (nodeType == JsonNodeType.NULL) {
			return null;
		}
		if (nodeType == JsonNodeType.NUMBER || Number.class.isAssignableFrom(type)) {
			return getNumberValue(jsonValue, type);
		}
		if (type.isEnum()) {
			return Arrays.stream(type.getEnumConstants())
				.filter(it -> it.toString().equals(jsonValue.asText()))
				.findAny()
				.orElseThrow(() -> new IllegalStateException("Invalid enum: " + type.getName() + "." + jsonValue.asText()));
		}
		
		if (type == LocalDate.class) {
			return LocalDate.parse(jsonValue.asText());
		}
		
		if (nodeType == JsonNodeType.STRING) {
			return jsonValue.asText();
		}
		if (nodeType == JsonNodeType.BOOLEAN) {
			return jsonValue.asBoolean();
		}
		if (type.isArray() || Collection.class.isAssignableFrom(type)) {
			CollectionWrapper wrapper = new CollectionWrapper();
			if (nodeType == JsonNodeType.ARRAY) {
				wrapper.items = (ArrayNode) jsonValue;
				wrapper.size = wrapper.items.size();
			} else if (nodeType == JsonNodeType.OBJECT){
				wrapper.size = jsonValue.get("size").asInt();
				wrapper.items = (ArrayNode) jsonValue.get("items");
			}
			return wrapper;
		}
		if (jsonValue.isArray() && (!type.isArray() && !Collection.class.isAssignableFrom(type))) {
			String expectedType = "Array or Collection";
			
			fail(
				"Invalid data type for field: " + context.getFullFieldName()
				+ " ==> expected: " + expectedType + " but was " + type.getName()
			);
		}
		if (nodeType == JsonNodeType.OBJECT) {
			try {
				return YAML_MAPPER.treeToValue(jsonValue, type);
			} catch (JsonProcessingException e) {
				throw new RuntimeException("Unable to get raw value to field: " + context.getFullFieldName(), e);
			}
		}
		
		throw new RuntimeException("Unable to get json value, because type is not mapped: " + jsonValue);
	}

	private Number getNumberValue(JsonNode jsonValue, Class<?> type) {
		if (type == BigDecimal.class) {
			return new BigDecimal(jsonValue.asText());
		}
		if (type == Long.class) {
			return jsonValue.asLong();
		}
		if (type == Integer.class) {
			return jsonValue.asInt();
		}
		if (type == Double.class) {
			return jsonValue.asDouble();
		}
		if (type == Float.class) {
			return Float.parseFloat(jsonValue.asText());
		}
		throw new IllegalStateException("Number Type not mapped: " + type.getName());
	}

}
