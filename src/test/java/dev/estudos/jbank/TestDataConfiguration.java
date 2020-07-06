package dev.estudos.jbank;

import org.aspectj.apache.bcel.util.ClassPath;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;

@Configuration

public class TestDataConfiguration {
	@Bean
	public Jackson2RepositoryPopulatorFactoryBean repositoryPopulatorFactoryBean() {
		Jackson2RepositoryPopulatorFactoryBean factory;
		factory = new  Jackson2RepositoryPopulatorFactoryBean();
		factory.setResources(new Resource[] {new ClassPathResource("dados.json")});
		
		return factory;
	}

}
