package dev.estudos.jbank;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

import dev.digytal.ymltest.YmlTestCaseConfiguration;

@Configuration
@ImportAutoConfiguration(YmlTestCaseConfiguration.class)
public class YmlTestConfiguration {

}
