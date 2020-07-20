package dev.digytal.ymltest;

import javax.persistence.EntityManager;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class YmlTestCaseConfiguration {
	
	@Bean
	public YmlTestCaseLoader ymlTestCaseLoader(YmlContextDataPopulator dataPopulator) {
		return new YmlTestCaseLoader("scenarios", dataPopulator);
	}
	
	@Bean
	public YmlContextDataPopulator contextDataPopulator(ApplicationContext applicationContext) {
		return new YmlContextDataPopulator(applicationContext, YmlObjectMapperFactory.getYamlMapper());
	}
	
	@Bean
	public DataPopulator dataPopulator(EntityManager em) {
		return new DataPopulator(em);
	}
	
}
