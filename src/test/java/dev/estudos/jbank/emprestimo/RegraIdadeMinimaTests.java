package dev.estudos.jbank.emprestimo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import dev.digytal.ymltest.YmlTestCase;
import dev.digytal.ymltest.YmlTestCaseLoader;
import dev.estudos.jbank.service.EmprestimoService;

@SpringBootTest
public class RegraIdadeMinimaTests {
    
	@Autowired
	YmlTestCaseLoader testCaseLoader;

	@Autowired
	EmprestimoService emprestimoService;
    
	@Test
	void given_clienteComIdadeMenorQueAMinimaPermitida_then_deveSerRejeitado() {
		execTestCase();
	}
	
	@Test
	void given_clienteComIdadeIgualAMinimaPermitida_then_deveSerAprovada() {
		execTestCase();
	}
	
	@Test
	void given_clienteComIdadeMaiorQueAMinimaPermitida_then_deveSerAprovada() {
		execTestCase();
	}
	
	private void execTestCase() {
		YmlTestCase testCase = testCaseLoader.loadOfMethodName();
		
		testCase.process(emprestimoService::contratar);		
	}
}