package dev.estudos.jbank.emprestimo;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;

import dev.estudos.jbank.model.PagamentoParcela;
import dev.estudos.jbank.repository.PagamentoRepository;
import dev.estudos.jbank.service.PagamentoParcelaService;

/**
 * O objetivo dessa classe eh testar os cenarios de pagamento de emprestimo.
 * mano
 * eu tenho que criar uma classe pagamento de parcela ou posso usar a classe parcel?
 * 
 * Pagamento de Parcelas

  * parcela a vencer com o mesmo valor da parcela (deve pagar com sucesso)
  * parcela em atraso com o mesmo valor da parcela (deve emitir erro)
  * parcela em atraso com o valor da parcela + valor da multa e juros calculado (deve pagar com sucesso)
  * parcela vencida no sábado com pagamento na segunda com o valor igual (deve permitir)
  * parcela vencida no domingo com pagamento na segunda com o valor igual (deve permitir)
  * parcela vencida no sábado com pagamento na terça com o valor igual (deve emitir erro)
  * parcela vencida no domingo com pagamento na terça com o valor da parcela + juros + multa (deve permitir)
 *
 */
public class PagamentoParcelaTests {
	
	@Autowired
	PagamentoParcelaService pagamentoParcelaService;
	@Autowired
	PagamentoRepository pagamentoRepository;
	
	/**
	 * cenario de teste para pagamento de parcela que esteja em dia, nao vai cobrar juros e multa
	 * o cliente vai pagar o mesmo valor da parcela, e o sistema deve aceitar o pagamento
	 */
	public void given_parcelaAVencerComOMesmoValorDaParcela_then_devePagarComSucesso() {
		
		// cliente quer pagar a parcela 3 do emprestimo 100
		String numDocumento = "100/3"; 
		BigDecimal valorPago = new BigDecimal("800.00");
		
		PagamentoParcela pagamento = pagamentoParcelaService.pagar(numDocumento, valorPago);
		
		// por isso eu uso o arquivo yml, para evitar esses asserts manuais. mas pode usar assim tmb. sem arquivo.
		assertNotNull(pagamento);
		assertNotNull(pagamento.getId(), "O id do pagamento deveria ter sido gerado");
		assertEquals(100, pagamento.getIdEmprestimo());
		assertEquals(3, pagamento.getParcela().getNumero());
		assertEquals(new BigDecimal("800.00"), pagamento.getValorPago());
		assertEquals(new BigDecimal("800.00"), pagamento.getValorParcela());
		assertEquals(BigDecimal.ZERO, pagamento.getValorJuros());
		assertEquals(BigDecimal.ZERO, pagamento.getValorMulta());
		assertNotNull(pagamento.getDataHoraPagamento(), "A data/hora de pagamento deveria ser preenchida");
		
		// Verifica se o pagamento foi registrado no banco de dados
		PagamentoParcela salvo = pagamentoRepository.getById(pagamento.getId()).orElse(null);
		
		assertNotNull(salvo, "O pagamento deveria ser registrado no banco de dados");		
	}
	
}
