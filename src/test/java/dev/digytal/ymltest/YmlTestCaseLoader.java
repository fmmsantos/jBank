package dev.digytal.ymltest;

import java.lang.reflect.Method;

import javax.persistence.EntityManager;
import javax.persistence.Table;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

public class YmlTestCaseLoader {
	
	String scenariosFolder = "";
	YmlContextDataPopulator dataPopulator;
	
	public YmlTestCaseLoader(YmlContextDataPopulator dataPopulator) {
		this.dataPopulator = dataPopulator;
	}
	
	public YmlTestCaseLoader(String scenariosFolder, YmlContextDataPopulator dataPopulator) {
		this.dataPopulator = dataPopulator;
		this.scenariosFolder = scenariosFolder;
	}
	
	public YmlTestCase load(String resourceName) {
		YmlTestCase testCase = YmlTestCase.ofResource(scenariosFolder + "/" + resourceName);
		
		JsonNode contextData = testCase.getContextData();
		if (contextData != null) {
			dataPopulator.populate(contextData);
		}
		
		return testCase;
	}

	public YmlTestCase loadOfMethodName() {
		StackTraceElement item = null;
		String simpleClassName = null;
		
		for (StackTraceElement stElement : Thread.currentThread().getStackTrace()) {
			try {
				Class<?> clazz = Class.forName(stElement.getClassName());
				Method method = clazz.getDeclaredMethod(stElement.getMethodName());
				boolean isTest = method.isAnnotationPresent(Test.class);
				
				if (isTest) {
					item = stElement;
					simpleClassName = clazz.getSimpleName();
					break;
				}
			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
				throw new IllegalStateException(e);
			}
		}
		
		if (item == null) {
			throw new IllegalStateException("Unit Test not found in thread");
		}
		
		String resourceName = item.getClassName() + "/" + item.getMethodName() + ".yml";
		String simpleResourceName = simpleClassName + "/" + item.getMethodName() + ".yml";
		
		try {
			return load(resourceName);
		} catch(IllegalArgumentException e) {
			if (e.getMessage().startsWith("Resource not found")) {
				return load(simpleResourceName);
			}
			throw e;
		}
	}

	public void clearEntities(Class<?> ... classes) {
		DataPopulator dataPopulator = this.dataPopulator.applicationContext.getBean(DataPopulator.class); 
		for (Class<?> clazz : classes) {
			dataPopulator.truncate(clazz);
		}
	}
	
}
