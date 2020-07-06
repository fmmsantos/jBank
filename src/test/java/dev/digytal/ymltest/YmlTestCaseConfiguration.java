package dev.digytal.ymltest;

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
	
}
