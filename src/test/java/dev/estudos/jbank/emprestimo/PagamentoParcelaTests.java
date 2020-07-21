package dev.estudos.jbank.emprestimo;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import dev.digytal.ymltest.YmlTestCase;
import dev.digytal.ymltest.YmlTestCaseLoader;
import dev.estudos.jbank.model.PagamentoParcela;
import dev.estudos.jbank.model.Parcela;
import dev.estudos.jbank.repository.PagamentoRepository;
import dev.estudos.jbank.repository.ParcelaRepository;
import dev.estudos.jbank.service.PagamentoParcelaService;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * O objetivo dessa classe eh testar os cenarios de pagamento de emprestimo.
 * mano eu tenho que criar uma classe pagamento de parcela ou posso usar a
 * classe parcel?
 * 
 * Pagamento de Parcelas
 * 
 * parcela a vencer com o mesmo valor da parcela (deve pagar com sucesso)
 * parcela em atraso com o mesmo valor da parcela (deve emitir erro) parcela em
 * atraso com o valor da parcela + valor da multa e juros calculado (deve pagar
 * com sucesso) parcela vencida no sábado com pagamento na segunda com o valor
 * igual (deve permitir) parcela vencida no domingo com pagamento na segunda com
 * o valor igual (deve permitir) parcela vencida no sábado com pagamento na
 * terça com o valor igual (deve emitir erro) parcela vencida no domingo com
 * pagamento na terça com o valor da parcela + juros + multa (deve permitir)
 *
 */
@SpringBootTest()
public class PagamentoParcelaTests {

	@Autowired
	YmlTestCaseLoader testCaseLoader;

	@Autowired
	PagamentoParcelaService pagamentoParcelaService;
	@Autowired
	PagamentoRepository pagamentoRepository;
	@Autowired
	ParcelaRepository parcelaRepository;

	/**
	 * cenario de teste para pagamento de parcela que esteja em dia, nao vai cobrar
	 * juros e multa o cliente vai pagar o mesmo valor da parcela, e o sistema deve
	 * aceitar o pagamento
	 */
	@Test
	public void given_parcelaAVencerComOMesmoValorDaParcela_then_devePagarComSucesso() {
		// temos que carregar os dados de teste
		YmlTestCase testCase = testCaseLoader.loadOfMethodName();

		// cliente quer pagar a parcela 3 do emprestimo 100
		String numDocumento = testCase.getInputItem("numDocumento");
		BigDecimal valorAPagar = new BigDecimal(testCase.getInputItem("valorAPagar"));

		PagamentoParcela pagamento = pagamentoParcelaService.pagar(numDocumento, valorAPagar);

		// faz as verificaoes de acordo com o que foi definido no arquivo yml
		testCase.assertOutput(pagamento);

		// por isso eu uso o arquivo yml, para evitar esses asserts manuais. mas pode
		// usar assim tmb. sem arquivo.
		assertNotNull(pagamento.getId(), "O id do pagamento deveria ter sido gerado");
		assertNotNull(pagamento.getDataPagamento(), "A data/hora de pagamento deveria ser preenchida");

		// Verifica se o pagamento foi registrado no banco de dados
		Optional<PagamentoParcela> salvo = pagamentoRepository.findById(pagamento.getId());

		assertNotNull(salvo, "O pagamento deveria ser registrado no banco de dados");
	}

	@Test
	public void given_parcelaEmAtrasoComOMesmoValorDaParcela_then_deveEmitirErro() {
		YmlTestCase testCase = testCaseLoader.loadOfMethodName();
		String numDocumento = testCase.getInputItem("numDocumento");
		BigDecimal valorAPagar = new BigDecimal(testCase.getInputItem("valorAPagar"));

		PagamentoParcela pagamento = pagamentoParcelaService.pagar(numDocumento, valorAPagar);

		// faz as verificaoes de acordo com o que foi definido no arquivo yml
		testCase.assertOutput(pagamento);
	
	}
/*
	@Test
	public void given_parcelaEmAtrasoComOValorDaParcelaMaisValorDaMultaEJurosCalculado_then_DevePagarComSucesso() {
		YmlTestCase testCase = testCaseLoader.loadOfMethodName();

		// cliente quer pagar a parcela 3 do emprestimo 100
		String numDocumento = testCase.getInputItem("numDocumento");
		BigDecimal valorAPagar = new BigDecimal(testCase.getInputItem("valorAPagar"));

		PagamentoParcela pagamento = pagamentoParcelaService.pagar(numDocumento, valorAPagar);

		// faz as verificaoes de acordo com o que foi definido no arquivo yml
		testCase.assertOutput(pagamento);

		
	}

	@Test
	public void given_parcelaVencidaNoSábadoComPagamentoNaSegundaComOValorIgual_then_devePermitir() {
		YmlTestCase testCase = testCaseLoader.loadOfMethodName();

		// cliente quer pagar a parcela 3 do emprestimo 100
		String numDocumento = testCase.getInputItem("numDocumento");
		BigDecimal valorAPagar = new BigDecimal(testCase.getInputItem("valorAPagar"));

		PagamentoParcela pagamento = pagamentoParcelaService.pagar(numDocumento, valorAPagar);

		// faz as verificaoes de acordo com o que foi definido no arquivo yml
		testCase.assertOutput(pagamento);

	
	}

	@Test
	public void given_parcelaVencidaNoDomingoComPagamentoNaSegundaComOValorIgual_then_devePermitir() {
		YmlTestCase testCase = testCaseLoader.loadOfMethodName();

		// cliente quer pagar a parcela 3 do emprestimo 100
		String numDocumento = testCase.getInputItem("numDocumento");
		BigDecimal valorAPagar = new BigDecimal(testCase.getInputItem("valorAPagar"));

		PagamentoParcela pagamento = pagamentoParcelaService.pagar(numDocumento, valorAPagar);

		// faz as verificaoes de acordo com o que foi definido no arquivo yml
		testCase.assertOutput(pagamento);

	
	}

	@Test
	public void given_parcelaVencidaNoSabadoComPagamentoNaTercaComOValorIgual_then_DeveEmitirErro() {
		YmlTestCase testCase = testCaseLoader.loadOfMethodName();

		// cliente quer pagar a parcela 3 do emprestimo 100
		String numDocumento = testCase.getInputItem("numDocumento");
		BigDecimal valorAPagar = new BigDecimal(testCase.getInputItem("valorAPagar"));

		PagamentoParcela pagamento = pagamentoParcelaService.pagar(numDocumento, valorAPagar);

		// faz as verificaoes de acordo com o que foi definido no arquivo yml
		testCase.assertOutput(pagamento);

		
	}

	@Test
	public void given_parcelaVencidaNoDomingoComPagamentoNaTercaComOValorDaParcelaMaisJurosMaisMulta_then_devePermitir() {
		YmlTestCase testCase = testCaseLoader.loadOfMethodName();

		// cliente quer pagar a parcela 3 do emprestimo 100
		String numDocumento = testCase.getInputItem("numDocumento");
		BigDecimal valorAPagar = new BigDecimal(testCase.getInputItem("valorAPagar"));

		PagamentoParcela pagamento = pagamentoParcelaService.pagar(numDocumento, valorAPagar);

		// faz as verificaoes de acordo com o que foi definido no arquivo yml
		testCase.assertOutput(pagamento);

		
		
	}
	*/

}
