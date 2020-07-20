package dev.estudos.jbank.emprestimo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import dev.digytal.ymltest.YmlTestCase;
import dev.digytal.ymltest.YmlTestCaseLoader;
import dev.estudos.jbank.dto.SolicitacaoEmprestimoDTO;
import dev.estudos.jbank.model.Emprestimo;
import dev.estudos.jbank.model.Parcela;
import dev.estudos.jbank.model.StatusParcela;
import dev.estudos.jbank.repository.ParcelaRepository;
import dev.estudos.jbank.service.EmprestimoService;

@SpringBootTest()
public class StatusParcelaTests {

	@Autowired
	YmlTestCaseLoader testCaseLoader;

	@Autowired
	EmprestimoService emprestimoService;
	
	@Autowired
	ParcelaRepository parcelaRepository;

	Long idEmprestimo;
	Integer numParcela;
	
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
		execTestCase();
		
		// verifica se o status da parcela foi atualizado no banco de dados
		Parcela parcelaSalva = buscarParcelaSalva();
		
		assertNotNull(parcelaSalva, "A parcela deveria existir no banco de dados") ;
		assertEquals(StatusParcela.EM_ATRASO, parcelaSalva.getStatus(), "A parcela salva deveria está EM ATRASO");
	}

	@Test
	void given_processarNaSegundaOStatusDeUmaParcelaVencidaNoSabadoOStatusDeveSerAvencer() {
		execTestCase();
		
		// verifica se o status da parcela foi atualizado no banco de dados
		Parcela parcelaSalva = buscarParcelaSalva();
		
		assertNotNull(parcelaSalva, "A parcela deveria existir no banco de dados") ;
		assertEquals(StatusParcela.A_VENCER, parcelaSalva.getStatus(), "A parcela salva deveria está A_VENCER");
	}

	@Test
	void given_processarNaTercaOStatusDeUmaParcelaVencidaNoDomingoOStatusDeveSerEmAtraso() {
		execTestCase();
		
		// verifica se o status da parcela foi atualizado no banco de dados
		Parcela parcelaSalva = buscarParcelaSalva();
		
		assertNotNull(parcelaSalva, "A parcela deveria existir no banco de dados") ;
		assertEquals(StatusParcela.EM_ATRASO, parcelaSalva.getStatus(), "A parcela salva deveria está EM ATRASO");
	}

	private void execTestCase() {
		YmlTestCase testCase = testCaseLoader.loadOfMethodName();
		
		idEmprestimo = testCase.getInputArgs().getLong("idEmprestimo");
		numParcela = testCase.getInputArgs().getInt("numParcela");
		
		// aqui nao tem valor, o meu objetivo é calcular o status da parcela. se ela está A VENCER, EM_ATRASO
		Parcela parcela = emprestimoService.processarStatusParcela(idEmprestimo, numParcela);
		
		testCase.assertOutput(parcela);
	}
	
	private Parcela buscarParcelaSalva() {
		return parcelaRepository.findByEmprestimoIdAndNumero(idEmprestimo, numParcela);
	}

	private void execTestCaseWithError() {
		YmlTestCase testCase = testCaseLoader.loadOfMethodName();

		testCase.process(emprestimoService::contratar);
	}
}
