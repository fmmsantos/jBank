package dev.estudos.jbank.emprestimo;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import dev.digytal.ymltest.YmlTestCase;
import dev.digytal.ymltest.YmlTestCaseLoader;
import dev.estudos.jbank.model.Emprestimo;
import dev.estudos.jbank.service.EmprestimoService;

@SpringBootTest
public class SimularEmprestimoTests {
	
	@Autowired
	EmprestimoService emprestimoService;
	
	@Autowired
	YmlTestCaseLoader testCaseLoader;
	
	@Test
	void given_clienteComLimiteMaiorQueOValorSolicitado_then_deveSerAprovado() {
		execTestCase();
	}
	
	@Test
	void given_clienteComLimiteMenorQueOValorSolicitado_then_deveSerRejeitado() {
		execTestCase();
	}
	
	@Test
    void given_clienteComRendaMensalSuperior3Vezes_then_deveSerAprovado() {
		execTestCase();
	}
	
	@Test
	void given_clienteSemLimiteERendaMensalInferior_then_deveEntrarEmAnalise() {
		execTestCase();
	}
	
	@Test
	void given_taxaDeJurosDoClienteNaoDefinida_then_deveUsarTaxaPadrao() {
		execTestCase();
	}
	
	@Test
	void given_taxaDeJurosDoClienteDefinida_then_deveUsarATaxaDoCliente() {
		execTestCase();
	}
	
	@Test
	void given_qtdMaximaDeParcelasSuperada_then_deveRetornarErro() {
		execTestCaseWithError();
	}
	
	@Test
	void given_valorDaParcelaMenorQueOValorMinimoConfigurado_then_deveEmitirErro() {
		execTestCaseWithError();
	}
	
	private void execTestCase() {
		YmlTestCase testCase = testCaseLoader.loadOfMethodName();
		
		Emprestimo output = testCase.process(emprestimoService::simular);
		
		assertNull(output.getId(), "O id do emprestimo nao deve ser gerado quando for simulacao");
	}

	private void execTestCaseWithError() {
		YmlTestCase testCase = testCaseLoader.loadOfMethodName();
		
		testCase.process(emprestimoService::simular);
	}
	
}
