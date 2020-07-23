package dev.estudos.jbank.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
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
	
	@Autowired
	private EmprestimoServiceImpl emprestimoService;
	

	@Override
	public PagamentoParcela pagar(String numeroDocumento, BigDecimal valorPago) {

		String parcelaIdEmprestimo[] = numeroDocumento.split("/");
		LocalDate incrementarData = null;
		String emprestimo = parcelaIdEmprestimo[0];
		String parcelaNumero = parcelaIdEmprestimo[1];

		int numero = Integer.parseInt(parcelaNumero);
		Long idEmprestimo = Long.parseLong(emprestimo);

		Optional<Emprestimo> empres = repository.findById(idEmprestimo);

		Parcela parcela = emprestimoService.getParcela(idEmprestimo, numero);

		// vc tem que trabalhar com excecoes para indicar o que acontece no codigoo
		// pq NullPointerexception ninguem sabe o que eh.
		// vc tem que mandar mensagens explicativas
		if (parcela == null) {
			throw new IllegalArgumentException(
					"Parcela " + numero + " nao encontrada para o emprestimo " + idEmprestimo);
		}

		PagamentoParcela pagamento = new PagamentoParcela();
		Configuracao configuracao = confRepository.getConfiguracao();

		if (parcela.getNumero() == numero && parcela.getStatus() != StatusParcela.PAGO) {

			pagamento.setDataPagamento(FlexibleCalendar.currentDate());
			pagamento.setIdEmprestimo(idEmprestimo);
			pagamento.setNumeroParcela(parcelaNumero);
			pagamento.setValorParcela(parcela.getValorTotal());
			pagamento.setParcela(parcela);
			pagamento.getParcela().setNumero(numero);

			if (parcela.getDataVencimento().getDayOfWeek() == DayOfWeek.SATURDAY) {
				incrementarData = parcela.getDataVencimento().plusDays(2);
				parcela.setDataVencimento(incrementarData);
			}
			if (parcela.getDataVencimento().getDayOfWeek() == DayOfWeek.SUNDAY) {
				incrementarData = parcela.getDataVencimento().plusDays(1);
				parcela.setDataVencimento(incrementarData);

				if (parcela.getDataVencimento().isBefore(FlexibleCalendar.currentDate())) {
					parcela.setStatus(StatusParcela.EM_ATRASO);
				} else {
					parcela.setStatus(StatusParcela.A_VENCER);
				}

			}
			
			if (parcela.getStatus() == StatusParcela.EM_ATRASO && valorPago.compareTo(parcela.getValorTotal()) <= 0)

				throw new PagamentoNaoAceitoException("VALOR DO PAGAMENTO MENOR QUE O VALOR DA PARCELA");

		}
		if (parcela.getStatus() == StatusParcela.EM_ATRASO) {
			
			pagamento.setValorJuros(
					configuracao.getJurosDeMora().divide(new BigDecimal(100), 3, RoundingMode.HALF_EVEN));
			pagamento.setValorMulta(
					configuracao.getMultaDeMora().divide(new BigDecimal(100), 3, RoundingMode.HALF_EVEN));

			int hoje1 = pagamento.getDataPagamento().getDayOfMonth();
			int dia = parcela.getDataVencimento().getDayOfMonth();
			int diasDeAtraso = hoje1 - dia;

			BigDecimal diasVencidJuros = pagamento.getValorJuros().multiply(new BigDecimal(diasDeAtraso));

			BigDecimal arredondar1 = diasVencidJuros.divide(new BigDecimal(30), 3, RoundingMode.HALF_DOWN);

			BigDecimal totalJuros = arredondar1.multiply(parcela.getValorTotal());
			BigDecimal totalMulta = parcela.getValorTotal().multiply(pagamento.getValorMulta());
			pagamento.setValorPago(totalJuros.add(totalMulta).add(parcela.getValorTotal()));
			pagamento.setValorJuros(totalJuros);
			pagamento.setValorMulta(totalMulta);

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
