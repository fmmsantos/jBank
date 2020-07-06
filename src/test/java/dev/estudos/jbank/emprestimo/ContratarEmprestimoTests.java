package dev.estudos.jbank.emprestimo;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import dev.digytal.ymltest.YmlTestCase;
import dev.digytal.ymltest.YmlTestCaseLoader;
import dev.estudos.jbank.model.Emprestimo;
import dev.estudos.jbank.repository.EmprestimoRepository;
import dev.estudos.jbank.service.EmprestimoService;

@SpringBootTest()
public class ContratarEmprestimoTests {
	
	@Autowired
	YmlTestCaseLoader testCaseLoader;

	@Autowired
	EmprestimoService emprestimoService;
	
	@Autowired
	EmprestimoRepository emprestimoRepository;
	
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
	void given_taxaDeJurosNaoDefinida_then_deveUsarTaxaPadrao() {
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
	void given_valorDaParcelaMenorQueAValorMinimoConfigurado_then_deveEmitirErro() {
		execTestCaseWithError();
	}
	
	private void execTestCase() {
		YmlTestCase testCase = testCaseLoader.loadOfMethodName();
		
		Emprestimo emprestimo = testCase.process(emprestimoService::contratar);
		
		assertNotNull(emprestimo.getId(), "O id do emprestimo devia ter sido gerado");
		
		Optional<Emprestimo> found = emprestimoRepository.findById(emprestimo.getId());
		
		assertTrue(found.isPresent(), "O emprestimo devia estar registrado no banco de dados");
	}
	
	private void execTestCaseWithError() {
		YmlTestCase testCase = testCaseLoader.loadOfMethodName();
		
		testCase.process(emprestimoService::contratar);		
	}
	
}
