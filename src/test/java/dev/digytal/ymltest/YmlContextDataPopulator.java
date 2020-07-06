package dev.digytal.ymltest;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.init.ResourceReader;
import org.springframework.data.repository.init.ResourceReaderRepositoryPopulator;
import org.springframework.data.repository.support.Repositories;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class YmlContextDataPopulator {
	
	ApplicationContext applicationContext;
	ObjectMapper mapper;
	
	public YmlContextDataPopulator(ApplicationContext applicationContext, ObjectMapper mapper) {
		this.applicationContext = applicationContext;
		this.mapper = mapper;
	}
	
	public void populate(JsonNode jsonNode) {
		if (jsonNode == null) {
			return;
		}
		
		if (!jsonNode.isArray()) {
			throw new IllegalStateException("Context Data shold be a JSON array, but was: " + jsonNode.getNodeType());
		}
		
		ArrayNode arrayNode = (ArrayNode) jsonNode;
		ArrayNode entitiesData = arrayNode.deepCopy();
		List<ObjectNode> staticsData = new ArrayList<>();
		Map<Integer, Class<?>> classes = new LinkedHashMap<>();
		
		int qtdRemoved = 0;
		int arraySize = arrayNode.size();
		for (int i = 0; i < arraySize; i++) {
			ObjectNode objNode = (ObjectNode) arrayNode.get(i);
			if (!objNode.has("_class")) {
				throw new IllegalStateException("Context Data item has no _class attribute");
			}
			String className = objNode.get("_class").asText();
			
			try {
				Class<?> itemClass = Class.forName(className);
				
				boolean isStatic = Modifier.isAbstract(itemClass.getModifiers()) || objNode.has("_static");
				
				if (isStatic) {
					entitiesData.remove(i - qtdRemoved);
					qtdRemoved++;
					objNode.put("_index", i);
					classes.put(i, itemClass);
					staticsData.add(objNode);
				}
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException("Invalid context data item " + i + ": " + e.toString());
			}
		}
		
		setStaticsData(staticsData, classes);
		
		ResourceReader resourceReader = new JsonResourceReader(mapper, entitiesData);
		ResourceReaderRepositoryPopulator populator = new ResourceReaderRepositoryPopulator(resourceReader);
		
		Resource resource = new ClassPathResource("");
		populator.setResources(resource);
		
		Repositories repositories = new Repositories(applicationContext);
		populator.populate(repositories);
	}

	private void setStaticsData(List<ObjectNode> staticsData, Map<Integer, Class<?>> classes) {
		for (ObjectNode node : staticsData) {
			Class<?> clazz = classes.get(node.get("_index").asInt());
			
			Iterator<String> fieldNames = node.fieldNames();
			while (fieldNames.hasNext()) {
				String fieldName = fieldNames.next();
				
				if (fieldName.startsWith("_")) {
					continue;
				}
				
				Method method = Arrays.stream(clazz.getMethods())
					.filter(it -> it.getName().equalsIgnoreCase("set" + fieldName) && it.getParameterCount() == 1)
					.findFirst()
					.orElse(null);
				
				Object argValue;
				Class<?> argType;
				try {
					if (method == null) {
						Field field = clazz.getField(fieldName);
						argType = field.getType();
						argValue = mapper.treeToValue(node.get(fieldName), argType);
						field.set(null, argValue);
					} else {
						argType = method.getParameters()[0].getType();
						argValue = mapper.treeToValue(node.get(fieldName), argType);
						method.invoke(null, argValue);
					}
				} catch (Exception e) {
					throw new IllegalStateException("Unable to set value to field " + clazz.getName() + "." + fieldName + ": " + e.toString(), e);
				}
			}
			
		}
	}
	
	public static class JsonResourceReader implements ResourceReader {
		
		ObjectMapper mapper;
		JsonNode jsonNode;
		String typeKey = "_class";
		
		JsonResourceReader(ObjectMapper mapper, JsonNode node) {
			this.mapper = mapper;
			this.jsonNode = node;
		}
		
		@Override
		public Object readFrom(Resource resource, ClassLoader classLoader) throws Exception {
			if (jsonNode.isArray()) {

				Iterator<JsonNode> elements = jsonNode.elements();
				List<Object> result = new ArrayList<>();

				while (elements.hasNext()) {
					JsonNode element = elements.next();
					result.add(readSingle(element, classLoader));
				}

				return result;
			}
			
			return readSingle(jsonNode, classLoader);
		}
		
		private Object readSingle(JsonNode node, @Nullable ClassLoader classLoader) throws IOException {

			JsonNode typeNode = node.findValue(typeKey);

			if (typeNode == null) {
				throw new IllegalArgumentException(String.format("Could not find type for type key '%s'!", typeKey));
			}

			String typeName = typeNode.asText();
			Class<?> type = ClassUtils.resolveClassName(typeName, classLoader);

			return mapper.readerFor(type).readValue(node);
		}
		
	}
	
}
