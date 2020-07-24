package dev.digytal.ymltest;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class YmlObjectMapperFactory {
	
	static final Module MODULE = new SimpleModule("ymlTestCaseModule")
		.setDeserializerModifier(new IgnorePropsDeserializerModifier("_class"));
	
	static final YAMLFactory FACTORY = new YAMLFactory();
	
	static final ObjectMapper YAML_MAPPER = new ObjectMapper(FACTORY)
		.registerModule(new JavaTimeModule())
		.registerModule(MODULE);
	
	public static ObjectMapper getYamlMapper() {
		return YAML_MAPPER;
	}
	
	public static YAMLFactory getYamlFactory() {
		return FACTORY;
	}
	
	public static class IgnorePropsDeserializerModifier extends BeanDeserializerModifier {
    	
		Set<String> ignoreProps = Collections.emptySet();
		
		public IgnorePropsDeserializerModifier(String ... ignoreFields) {
			this.ignoreProps = new HashSet<String>(java.util.Arrays.asList(ignoreFields));
		}
		
    	@Override
    	public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc,
    			JsonDeserializer<?> deserializer) {

    		if (deserializer instanceof BeanDeserializerBase) {
    			return ((BeanDeserializerBase) deserializer).withIgnorableProperties(ignoreProps);
    		}
    		
    		return deserializer;
    	}
    	
    }
	
}
