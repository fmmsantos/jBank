package dev.estudos.jbank.emprestimo;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import dev.digytal.ymltest.YmlTestCase;
import dev.digytal.ymltest.YmlTestCaseLoader;
import dev.estudos.jbank.model.PagamentoParcela;
import dev.estudos.jbank.repository.PagamentoRepository;
import dev.estudos.jbank.repository.ParcelaRepository;
import dev.estudos.jbank.service.PagamentoParcelaService;

/**
 * O objetivo dessa classe eh testar os cenarios de pagamento de emprestimo.
 * 
 * Cenários:
 * - parcela a vencer com o mesmo valor da parcela (deve pagar com sucesso)
 * - parcela em atraso com o mesmo valor da parcela (deve emitir erro) parcela em
 * - atraso com o valor da parcela + valor da multa e juros calculado (deve pagar com sucesso) 
 * - parcela vencida no sábado com pagamento na segunda com o valor igual (deve permitir) 
 * - parcela vencida no domingo com pagamento na segunda com o valor igual (deve permitir) 
 * - parcela vencida no sábado com pagamento na terça com o valor igual (deve emitir erro) 
 * - parcela vencida no domingo com pagamento na terça com o valor da parcela + juros + multa (deve permitir)
 */
@SpringBootTest
@Sql(executionPhase=ExecutionPhase.AFTER_TEST_METHOD,scripts="classpath:/reset-data.sql")
public class PagamentoParcelaTests {

	@Autowired
	YmlTestCaseLoader testCaseLoader;

	@Autowired
	PagamentoParcelaService pagamentoParcelaService;
	@Autowired
	PagamentoRepository pagamentoRepository;
	@Autowired
	ParcelaRepository parcelaRepository;


	private YmlTestCase testCase;
	private String numDocumento;
	private BigDecimal valorAPagar;
	private PagamentoParcela output;


	/**
	 * cenario de teste para pagamento de parcela que esteja em dia, nao vai cobrar
	 * juros e multa o cliente vai pagar o mesmo valor da parcela, e o sistema deve
	 * aceitar o pagamento
	 */
	@Test
	public void given_parcelaAVencerComOMesmoValorDaParcela_then_devePagarComSucesso() {
		executarTestCase();
		verificarDadosSalvos();
	}

	@Test
	public void given_parcelaEmAtrasoComOMesmoValorDaParcela_then_deveEmitirErro() {
		executarTestCase();
	}

	@Test
	public void given_parcelaEmAtrasoComOValorDaParcelaMaisValorDaMultaEJurosCalculado_then_DevePagarComSucesso() {
		executarTestCase();
		verificarDadosSalvos();
	}

	@Test
	public void given_parcelaVencidaNoSábadoComPagamentoNaSegundaComOValorIgual_then_devePermitir() {
		executarTestCase();
		verificarDadosSalvos();
	}
	
	@Test
	public void given_parcelaVencidaNoDomingoComPagamentoNaSegundaComOValorIgual_then_devePermitir() {
		executarTestCase();
		verificarDadosSalvos();
	}
	
	@Test
	public void given_parcelaVencidaNoSabadoComPagamentoNaTercaComOValorIgual_then_DeveEmitirErro() {
		executarTestCase();
	}
	
	@Test
	public void given_parcelaVencidaNoDomingoComPagamentoNaTercaComOValorDaParcelaMaisJurosMaisMulta_then_devePermitir() {
		executarTestCase();
		verificarDadosSalvos();
	}
	
	@Test
	public void given_parcelaEmAtrasoCom17Dias_then_calcularJurosEMultaDeMora() {
		executarTestCase();
		verificarDadosSalvos();
	}
	
	@Test
	public void given_parcelaEmAtrasoCom40Dias_then_calcularJurosEMultaDeMora() {
		executarTestCase();
		verificarDadosSalvos();
	}
	
	@Test
	public void given_parcelaNaoExistente_then_DeveEmitirErro() {
		executarTestCase();
	}
	
	@Test
	public void given_numeroDoDocumentoInvalido_then_DeveEmitirErro() {
		executarTestCase();
	}
	
	private void executarTestCase() {
		List<YmlTestCase> testCases = testCaseLoader.loadMultipleOfMethodName();
		
		for (YmlTestCase current : testCases) {
			testCase = testCaseLoader.loadData(current);
			
			numDocumento = testCase.getInputItem("numDocumento");
			valorAPagar = new BigDecimal(testCase.getInputItem("valorAPagar"));
			
			output = testCase.process(() -> {
				return pagamentoParcelaService.pagar(numDocumento, valorAPagar);
			});
		}
	}
	
	private void verificarDadosSalvos() {
		// por isso eu uso o arquivo yml, para evitar esses asserts manuais. mas pode
		// usar assim tmb. sem arquivo.
		assertNotNull(output.getId(), "O id do pagamento deveria ter sido gerado");
		assertNotNull(output.getDataPagamento(), "A data/hora de pagamento deveria ser preenchida");
	
		// Verifica se o pagamento foi registrado no banco de dados
		Optional<PagamentoParcela> salvo = pagamentoRepository.findById(output.getId());
	
		assertNotNull(salvo, "O pagamento deveria ser registrado no banco de dados");
		
		testCase.assertOutput(salvo.get());
	}
	

}
