package dev.digytal.ymltest;

import javax.persistence.EntityManager;

import org.springframework.transaction.annotation.Transactional;

public class DataPopulator {
	
	EntityManager entityManager;
	
	public DataPopulator(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	@Transactional
	public void populate(Object entity) {
		entityManager.merge(entity);
	}
	
}
