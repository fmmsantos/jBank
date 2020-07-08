package dev.estudos.jbank.service;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.estudos.jbank.dto.SolicitacaoEmprestimoDTO;
import dev.estudos.jbank.model.Cliente;
import dev.estudos.jbank.model.Configuracao;
import dev.estudos.jbank.model.Emprestimo;
import dev.estudos.jbank.model.Parcela;
import dev.estudos.jbank.model.StatusEmprestimo;
import dev.estudos.jbank.repository.ClienteRepository;
import dev.estudos.jbank.repository.ConfiguracaoRepository;
import dev.estudos.jbank.repository.EmprestimoRepository;
import dev.estudos.jbank.repository.ParcelaRepository;
import dev.estudos.jbank.utils.FlexibleCalendar;

@Service
public class EmprestimoServiceImpl implements EmprestimoService {

	@Autowired
	private EmprestimoRepository repository;
	@Autowired
	private ClienteRepository repoCliente;

	@Autowired
	private ConfiguracaoRepository config;

	@Override
	public Emprestimo simular(SolicitacaoEmprestimoDTO solicitacao) {
		if (solicitacao.getCpfCnpjCliente() == null) {
			throw new IllegalArgumentException("Cliente não Registrado. Porfavor, realizar o cadastramento");
		}

		Emprestimo emprestimo = new Emprestimo();
		Configuracao configuracao = config.getConfiguracao();

		if (solicitacao != null) {

			Cliente cliente = repoCliente.findBycpfCnpj(solicitacao.getCpfCnpjCliente());
			if (cliente == null)
				throw new IllegalArgumentException(
						"Cliente não localizado com este cpf " + solicitacao.getCpfCnpjCliente());

			emprestimo.setCliente(cliente);
			emprestimo.setTaxaJuros(cliente.getTaxaJurosAoMes());
			
			
			if (cliente.getTaxaJurosAoMes() == null) {
			emprestimo.setTaxaJuros(configuracao.getTaxaJurosPadrao());
			}

			emprestimo.setValorSolicitado(solicitacao.getValorSolicitado());
			
			if (solicitacao.getQtdParcelas().compareTo(configuracao.getQtdMaximaParcelas()) >= 0) {
				throw new IllegalArgumentException("A quantidade de parcela não disponível ");
			}

			emprestimo.setTotalJuros(emprestimo.getValorSolicitado().multiply(emprestimo.getTaxaJuros())
					.multiply(new BigDecimal(solicitacao.getQtdParcelas())).divide(new BigDecimal(100), RoundingMode.HALF_EVEN));
			
			emprestimo.setTotalAPagar(solicitacao.getValorSolicitado().add(emprestimo.getTotalJuros()));
			
		
			emprestimo.setDataHoraSolicitacao(FlexibleCalendar.currentDateTime());
		}
		boolean aprovar = aprovar(solicitacao, emprestimo);
		boolean rejeitar = rejeitar(solicitacao, emprestimo);
		if (aprovar) {
			emprestimo.setStatus(StatusEmprestimo.APROVADO);
			List<Parcela> parcelass = new ArrayList<>();
			emprestimo.setDataHoraAprovacao(FlexibleCalendar.currentDateTime());

			for (int i = 0; i < solicitacao.getQtdParcelas(); i++) {
				Parcela parcelas = new Parcela();
				parcelas.setNumero(i);
				parcelas.setDataVencimento(FlexibleCalendar.currentDate().plusMonths(1L));
				
				if (parcelas.getValorTotal().compareTo(configuracao.getValorMinimoParcela()) < 0) {
					throw new IllegalArgumentException("O valor da parcela não pode ser menor que o valor mínimo da parcela ");
				}
				
				parcelas.setValorJuros(emprestimo.getTotalJuros().divide(new BigDecimal(solicitacao.getQtdParcelas()),2));
				parcelas.setValorTotal(emprestimo.getTotalAPagar().divide(new BigDecimal(solicitacao.getQtdParcelas()),2,RoundingMode.HALF_EVEN));
				parcelass.add(parcelas);
			}

		} else if (rejeitar) {
			emprestimo.setStatus(StatusEmprestimo.REJEITADO);
			emprestimo.setDataHoraRejeicao(FlexibleCalendar.currentDateTime());
		}
		else {
			emprestimo.setStatus(StatusEmprestimo.EM_ANALISE);
		}

		return emprestimo;
	}

	@Override
	public Emprestimo contratar(SolicitacaoEmprestimoDTO solicitacao) {

		if (solicitacao.getCpfCnpjCliente() == null) {
			throw new IllegalArgumentException("Cliente não Registrado. Porfavor, realizar o cadastramento");
		}

		Emprestimo emprestimo = new Emprestimo();

		if (solicitacao != null) {

			Cliente cliente = repoCliente.findBycpfCnpj(solicitacao.getCpfCnpjCliente());
			if (cliente == null)
				throw new IllegalArgumentException(
						"Cliente não localizado com este cpf " + solicitacao.getCpfCnpjCliente());

			emprestimo.setCliente(cliente);
			emprestimo.setTaxaJuros(cliente.getTaxaJurosAoMes());

			emprestimo.setValorSolicitado(solicitacao.getValorSolicitado());
			
			

			emprestimo.setTotalJuros(emprestimo.getValorSolicitado().multiply(emprestimo.getTaxaJuros())
					.multiply(new BigDecimal(solicitacao.getQtdParcelas())));
			emprestimo.setTotalAPagar(solicitacao.getValorSolicitado().add(emprestimo.getTotalJuros()));

			emprestimo.setDataHoraSolicitacao(FlexibleCalendar.currentDateTime());

			boolean aprovar = aprovar(solicitacao, emprestimo);
			boolean rejeitar = rejeitar(solicitacao, emprestimo);
			if (aprovar) {
				emprestimo.setStatus(StatusEmprestimo.APROVADO);
			} else if (rejeitar) {
				emprestimo.setStatus(StatusEmprestimo.REJEITADO);

			}
		}
		return emprestimo;
	}

	private boolean aprovar(SolicitacaoEmprestimoDTO solicitacao, Emprestimo emprestimo) {

		if (emprestimo.getCliente().getLimiteCredito().compareTo(solicitacao.getValorSolicitado()) >= 0) {
			emprestimo.setObservacao("LIMITE DE CRÉDITO ATENDE AO VALOR SOLICITADO");
			return true;
		} else if (emprestimo.getCliente().getRendaMesal()
				.compareTo(emprestimo.getParcelas().getValorPrincipal().multiply(new BigDecimal("3"))) > 0) {
			emprestimo.setObservacao("RENDA MENSAL DO CLIENTE 3X SUPERIOR A PARCELA");
			return true;
		}
		return true;
	}

	private boolean rejeitar(SolicitacaoEmprestimoDTO solicitacao, Emprestimo emprestimo) {

		if (emprestimo.getCliente().getLimiteCredito().compareTo(solicitacao.getValorSolicitado()) < 0) {
			emprestimo.setObservacao("LIMITE DE CRÉDITO NÃO ATENDE O VALOR SOLICITADO");
			return false;
		}
		return false;
	}

	@Override
	public boolean aprovar(SolicitacaoEmprestimoDTO solicitacao) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean rejeitar(SolicitacaoEmprestimoDTO solicitacao) {
		// TODO Auto-generated method stub
		return false;
	}

}
