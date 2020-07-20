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
	private Configuracao confRepository;
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
		PagamentoParcela pagamento = new PagamentoParcela();
		List<Parcela> parcelas = empres.get().getParcelas();
		for (Parcela parc : parcelas) {
			if (parc.getNumero() == numero && parc.getStatusParcela() != StatusParcela.PAGO) {

				pagamento.setDataHoraPagamento(FlexibleCalendar.currentDateTime());
				pagamento.setIdEmprestimo(idEmprestimo);
				pagamento.setNumeroParcela(parcelaNumero);
				pagamento.setValorParcela(parc.getValorTotal());

				if (parc.getStatusParcela() == StatusParcela.EM_ATRASO) {
					pagamento.setValorJuros(confRepository.getJurosDeMora());
					pagamento.setValorMulta(confRepository.getMultaDeMora());
					BigDecimal totalApagar = pagamento.getValorParcela().add(pagamento.getValorJuros())
							.add(pagamento.getValorMulta());
					pagamento.setValorPago(totalApagar);
					

				}
				if (pagamento.getValorPago().compareTo(valorPago) < 0) 
					
					throw new PagamentoNaoAceitoException("VALOR DO PAGAMENTO MENOR QUE O VALOR DA PARCELA");

				}
			else {
				
				pagamento.getDataHoraPagamento();
				pagamento.getValorPago();
				pagamentoRepo.save(pagamento);
				
				
			}
				
		}
		
		
		

		return pagamento;
	}

}
