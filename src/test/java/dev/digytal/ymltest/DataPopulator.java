package dev.digytal.ymltest;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DataPopulator {
	
	EntityManager entityManager;
	ObjectMapper mapper;
	
	public DataPopulator(ObjectMapper mapper, EntityManager entityManager) {
		this.mapper = mapper;
		this.entityManager = entityManager;
	}
	
	@Transactional
	public void populate(ObjectNode objectNode) {
		if (objectNode.has("_class")) {
			populateEntity(objectNode);
		} else if (objectNode.has("_table")) {
			populateTable(objectNode);
		}
	}
	
	protected void populateEntity(ObjectNode objectNode) {
		String className = objectNode.get("_class").asText();
		try {
			Class<?> clazz = Class.forName(className);
			Object entity = mapper.treeToValue(objectNode, clazz);
			entityManager.merge(entity);
		} catch (ClassNotFoundException | JsonProcessingException e) {
			throw new IllegalStateException("Unable to load entity for class: " + className, e);
		}
	}
	
	protected void populateTable(ObjectNode tableData) {
		List<String> columns = StreamSupport.stream(Spliterators.spliteratorUnknownSize(tableData.fieldNames(), Spliterator.ORDERED), false)
			.filter(it -> !it.startsWith("_"))
			.collect(Collectors.toList());
		
		String sql = String.format(
			"INSERT INTO %s ( %s ) VALUES ( %s )",
			tableData.get("_table").asText(), String.join(", ", columns), ":" + String.join(", :", columns) 
		); 
		Query query = entityManager.createNativeQuery(sql);
		
		columns.forEach(column -> {
			JsonNode valueNode = tableData.get(column);
			Object value = valueNode.asText();
			query.setParameter(column, value);
		});
		
		query.executeUpdate();		
	}
	
}
