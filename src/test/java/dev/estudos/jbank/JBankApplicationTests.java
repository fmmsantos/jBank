package dev.estudos.jbank;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import dev.estudos.jbank.service.EmprestimoService;

@SpringBootTest
class JBankApplicationTests {

	@Autowired
	EmprestimoService emprestimoService;
	
	@Test
	void contextLoads() {
	}

}
