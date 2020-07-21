package dev.estudos.jbank.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.estudos.jbank.exception.PagamentoNaoAceitoException;
import dev.estudos.jbank.model.Configuracao;
import dev.estudos.jbank.model.Emprestimo;
import dev.estudos.jbank.model.PagamentoParcela;
import dev.estudos.jbank.model.Parcela;
import dev.estudos.jbank.model.StatusParcela;
import dev.estudos.jbank.repository.ConfiguracaoRepository;
import dev.estudos.jbank.repository.EmprestimoRepository;
import dev.estudos.jbank.repository.PagamentoRepository;
import dev.estudos.jbank.repository.ParcelaRepository;
import dev.estudos.jbank.utils.FlexibleCalendar;

@Service
public class PagamentoServiceImpl implements PagamentoParcelaService {

	@Autowired
	private ParcelaRepository parcelaRepository;
	@Autowired
	private EmprestimoRepository repository;
	@Autowired
	private ConfiguracaoRepository confRepository;
	@Autowired
	private PagamentoRepository pagamentoRepo;

	@Override
	public PagamentoParcela pagar(String numeroDocumento, BigDecimal valorPago) {

		String parcelaIdEmprestimo[] = numeroDocumento.split("/");

		String emprestimo = parcelaIdEmprestimo[0];
		String parcelaNumero = parcelaIdEmprestimo[1];

		int numero = Integer.parseInt(parcelaNumero);
		Long idEmprestimo = Long.parseLong(emprestimo);

		Optional<Emprestimo> empres = repository.findById(idEmprestimo);

		Parcela parcela = parcelaRepository.findByEmprestimoIdAndNumero(idEmprestimo, numero);
		
		// vc tem que trabalhar com excecoes para indicar o que acontece no codigoo
		// pq NullPointerexception ninguem sabe o que eh. 
		// vc tem que mandar mensagens explicativas
		if (parcela == null) {
			throw new IllegalArgumentException("Parcela " + numero + " nao encontrada para o emprestimo " + idEmprestimo);
		}

		PagamentoParcela pagamento = new PagamentoParcela();

		if (parcela.getNumero() == numero && parcela.getStatus() != StatusParcela.PAGO) {

			pagamento.setDataPagamento(FlexibleCalendar.currentDate());
			pagamento.setIdEmprestimo(idEmprestimo);
			pagamento.setNumeroParcela(parcelaNumero);
			pagamento.setValorParcela(parcela.getValorTotal());
			pagamento.setParcela(parcela);
			pagamento.getParcela().setNumero(numero);
			Configuracao configuracao = confRepository.getConfiguracao();
			if (parcela.getStatus() == StatusParcela.EM_ATRASO) {
				pagamento.setValorJuros(configuracao.getJurosDeMora());
				pagamento.setValorMulta(configuracao.getMultaDeMora());
				BigDecimal totalApagar = pagamento.getValorParcela().add(pagamento.getValorJuros())
						.add(pagamento.getValorMulta());
				pagamento.setValorPago(totalApagar);

			}
			if (parcela.getStatus() == StatusParcela.EM_ATRASO && pagamento.getValorPago().compareTo(valorPago) < 0)

				throw new PagamentoNaoAceitoException("VALOR DO PAGAMENTO MENOR QUE O VALOR DA PARCELA");

		}
		if (parcela.getStatus() == StatusParcela.A_VENCER) {
			pagamento.getNumeroParcela();
			pagamento.getDataPagamento();
			pagamento.getValorParcela();
			pagamento.getValorMulta();
			pagamento.getValorJuros();
			pagamento.getIdEmprestimo();
			pagamento.getParcela();
			pagamento.getParcela().getNumero();
			pagamentoRepo.save(pagamento);

		}

		return pagamento;
	}

}
