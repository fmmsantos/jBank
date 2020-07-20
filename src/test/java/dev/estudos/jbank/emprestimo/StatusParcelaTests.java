package dev.estudos.jbank.emprestimo;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import dev.digytal.ymltest.YmlTestCase;
import dev.digytal.ymltest.YmlTestCaseLoader;
import dev.estudos.jbank.dto.SolicitacaoEmprestimoDTO;
import dev.estudos.jbank.model.Emprestimo;
import dev.estudos.jbank.model.Parcela;
import dev.estudos.jbank.model.StatusParcela;
import dev.estudos.jbank.repository.EmprestimoRepository;
import dev.estudos.jbank.service.EmprestimoService;

@SpringBootTest()
public class StatusParcelaTests {

	@Autowired
	YmlTestCaseLoader testCaseLoader;

	@Autowired
	EmprestimoService emprestimoService;

	@Autowired
	EmprestimoRepository emprestimoRepository;
	
	/**
	 * Para testar esse cenario eu preciso contratar um emprestimo.
	 */
	@Test
	void given_contratarUmEmprestimoComSucessoEVerificarSeGerouAsParcelasComOStatusAVencer() {
		YmlTestCase testCase = testCaseLoader.loadOfMethodName();

		SolicitacaoEmprestimoDTO solicitacao = testCase.getInput();

		Emprestimo output = emprestimoService.contratar(solicitacao);

		testCase.assertOutput(output);
	}
	
	/**
	 * Para testar esse cenario eu preciso chamar um metodo que processa o status de uma parcela
	 * Para esse cenario o correto é eu ter um metodo que faz o que ele pede. 
	 */
	@Test
	void given_umaParcelaVencida_then_deveAtualizarStatusParaEmAtraso() {
		YmlTestCase testCase = testCaseLoader.loadOfMethodName();
		
		// Pode ser assim
		Long idEmprestimo = testCase.getInputArgs().getLong("idEmprestimo");
		Integer numParcela = testCase.getInputArgs().getInt("numParcela");
		
		// aqui nao tem valor, o meu objetivo é calcular o status da parcela. se ela está A VENCER, EM_ATRASO
		Parcela parcela = emprestimoService.processarStatusParcela(idEmprestimo, numParcela);
		
		testCase.assertOutput(parcela);
		
		// verifica se o status da parcela foi atualizado no banco de dados
		Parcela parcelaSalva = emprestimoRepository.getParcela(idEmprestimo, numParcela);
		
		assertNotNull(parcela, "A parcela deveria existir no banco de dados") ;
		assertEquals(StatusParcela.EM_ATRASO, parcela.getStatusParcela());
	}

	@Test
	void given_processarNaSegundaOStatusDeUmaParcelaVencidaNoSabadoOStatusDeveSerAvencer() {
		execTestCase();
	}

	@Test
	void given_processarNaTercaOStatusDeUmaParcelaVencidaNoDomingoOStatusDeveSerEmAtraso() {
		execTestCase();
	}

	private void execTestCase() {

	}

	private void execTestCaseWithError() {
		YmlTestCase testCase = testCaseLoader.loadOfMethodName();

		testCase.process(emprestimoService::contratar);
	}
}
