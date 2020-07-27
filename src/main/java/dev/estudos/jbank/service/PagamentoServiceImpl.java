package dev.estudos.jbank.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
	private ConfiguracaoRepository confRepository;
	@Autowired
	private PagamentoRepository pagamentoRepo;

	@Autowired
	private EmprestimoServiceImpl emprestimoService;

	@Override
	public PagamentoParcela pagar(String numeroDocumento, BigDecimal valorPago) {

		if (numeroDocumento.matches("\\d*") || (!(numeroDocumento.contains("/")))) {
			throw new PagamentoNaoAceitoException("Numero do documento inválido");
		}

		String parcelaIdEmprestimo[] = numeroDocumento.split("/");

		String emprestimoId = parcelaIdEmprestimo[0];
		String parcelaNumero = parcelaIdEmprestimo[1];

		if (!emprestimoId.matches("\\d*")) {
			throw new PagamentoNaoAceitoException("Numero do documento inválido");
		}

		int numero = Integer.parseInt(parcelaNumero);
		Long idEmprestimo = Long.parseLong(emprestimoId);

		Parcela parcela = emprestimoService.getParcela(idEmprestimo, numero);

		if (parcela == null) {
			throw new PagamentoNaoAceitoException("Parcela nao encontrada");

		}

		PagamentoParcela pagamento = new PagamentoParcela();
		pagamento.setDataPagamento(FlexibleCalendar.currentDate());
		pagamento.setIdEmprestimo(idEmprestimo);
		pagamento.setNumeroParcela(parcelaNumero);
		pagamento.setValorParcela(parcela.getValorTotal());
		pagamento.setParcela(parcela);
		pagamento.getParcela().setNumero(numero);
		pagamentoRepo.save(pagamento);
		Configuracao configuracao = confRepository.getConfiguracao();

		if (parcela.getNumero() == numero && parcela.getStatus() != StatusParcela.PAGO) {

			parcela = emprestimoService.processarStatusParcela(idEmprestimo, numero);

			if (parcela.getStatus() == StatusParcela.EM_ATRASO && valorPago.compareTo(parcela.getValorTotal()) <= 0)

				throw new PagamentoNaoAceitoException("VALOR DO PAGAMENTO MENOR QUE O VALOR DA PARCELA");
		}
		if (parcela.getStatus() == StatusParcela.EM_ATRASO) {

			pagamento.setValorJuros(
					configuracao.getJurosDeMora().divide(new BigDecimal(100), 3, RoundingMode.HALF_EVEN));
			pagamento.setValorMulta(
					configuracao.getMultaDeMora().divide(new BigDecimal(100), 3, RoundingMode.HALF_EVEN));

			long dias = ChronoUnit.DAYS.between(pagamento.getDataPagamento(), parcela.getDataVencimento());
			int diaDeVencimento = (int) -dias;
			pagamento.setDiasAtraso(diaDeVencimento);

			BigDecimal diasVencidJuros = pagamento.getValorJuros().multiply(new BigDecimal(pagamento.getDiasAtraso()));

			BigDecimal arredondarJuros = diasVencidJuros.divide(new BigDecimal(30), 5, RoundingMode.HALF_EVEN);

			BigDecimal totalJuros = arredondarJuros.multiply(parcela.getValorTotal());
			BigDecimal totalMulta = parcela.getValorTotal().multiply(pagamento.getValorMulta());
			BigDecimal totalDeJuros = totalJuros.setScale(2, RoundingMode.HALF_EVEN);
			pagamento.setValorPago(totalDeJuros.add(totalMulta).add(parcela.getValorTotal()));
			pagamento.setValorJuros(totalDeJuros);
			pagamento.setValorMulta(totalMulta);
			pagamentoRepo.save(pagamento);

		}
		if (parcela.getStatus() == StatusParcela.A_VENCER) {

		}

		return pagamento;
	}

	public boolean verificacaoDoNumeroDocumento(String numeroDocumento) {
		char[] c = numeroDocumento.toCharArray();
		boolean d = true;

		for (int i = 0; i < c.length; i++)

			if (!Character.isDigit(c[i])) {
				d = true;
				break;
			}
		return false;
	}

}
