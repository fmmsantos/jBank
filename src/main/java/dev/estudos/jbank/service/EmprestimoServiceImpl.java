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

			if (solicitacao.getQtdParcelas().compareTo(configuracao.getQtdMaximaParcelas()) > 0) {
				throw new IllegalArgumentException("A quantidade de parcela não disponível ");
			}

			emprestimo.setTotalJuros(emprestimo.getValorSolicitado().multiply(emprestimo.getTaxaJuros())
					.multiply(new BigDecimal(solicitacao.getQtdParcelas()))
					.divide(new BigDecimal(100), RoundingMode.HALF_EVEN));
			emprestimo.setDataHoraSolicitacao(FlexibleCalendar.currentDateTime());
			emprestimo.setTotalAPagar(solicitacao.getValorSolicitado().add(emprestimo.getTotalJuros()));

			List<Parcela> parcelas = new ArrayList<>();
			for (int i = 0; i < solicitacao.getQtdParcelas(); i++) {
				Parcela parcela = new Parcela();
				parcela.setNumero(i);
				parcela.setDataVencimento(FlexibleCalendar.currentDate().plusMonths(1L));

				if (parcela.getValorTotal().compareTo(configuracao.getValorMinimoParcela()) < 0) {
					throw new IllegalArgumentException(
							"O valor da parcela não pode ser menor que o valor mínimo da parcela ");
				}

				parcela.setValorJuros(
						emprestimo.getTotalJuros().divide(new BigDecimal(solicitacao.getQtdParcelas()), 2));
				parcela.setValorTotal(emprestimo.getTotalAPagar().divide(new BigDecimal(solicitacao.getQtdParcelas()),
						2, RoundingMode.HALF_EVEN));
				parcelas.add(parcela);

			}
			emprestimo.setParcelas(parcelas);

		}
		boolean aprovar = aprovar(solicitacao, emprestimo);
		boolean rejeitar = rejeitar(solicitacao, emprestimo);

		if (aprovar) {
			emprestimo.setDataHoraAprovacao(FlexibleCalendar.currentDateTime());
			emprestimo.setStatus(StatusEmprestimo.APROVADO);

			emprestimo.getTotalAPagar();

		} else if (rejeitar) {
			emprestimo.setStatus(StatusEmprestimo.REJEITADO);
			emprestimo.setDataHoraRejeicao(FlexibleCalendar.currentDateTime());

		} else {
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

			emprestimo.setTotalJuros(emprestimo.getValorSolicitado().multiply(emprestimo.getTaxaJuros())
					.multiply(new BigDecimal(solicitacao.getQtdParcelas()))
					.divide(new BigDecimal(100), RoundingMode.HALF_EVEN));
			emprestimo.setDataHoraSolicitacao(FlexibleCalendar.currentDateTime());
			emprestimo.setTotalAPagar(solicitacao.getValorSolicitado().add(emprestimo.getTotalJuros()));

			List<Parcela> parcelas = new ArrayList<>();
			for (int i = 0; i < solicitacao.getQtdParcelas(); i++) {
				Parcela parcela = new Parcela();
				parcela.setNumero(i+1);
				parcela.setDataVencimento(FlexibleCalendar.currentDate().plusMonths(1L+i));
				
				parcela.setValorPrincipal(emprestimo.getValorSolicitado().divide(new BigDecimal(solicitacao.getQtdParcelas()),2,RoundingMode.HALF_EVEN));

				if (parcela.getValorTotal().compareTo(configuracao.getValorMinimoParcela()) < 0) {
					throw new IllegalArgumentException(
							"O valor da parcela não pode ser menor que o valor mínimo da parcela ");
				}

				parcela.setValorJuros(
						emprestimo.getTotalJuros().divide(new BigDecimal(solicitacao.getQtdParcelas()), 2));
				parcela.setValorTotal(emprestimo.getTotalAPagar().divide(new BigDecimal(solicitacao.getQtdParcelas()),
						2, RoundingMode.HALF_EVEN));
				parcelas.add(parcela);

			}
			emprestimo.setParcelas(parcelas);

		}
		boolean aprovar = aprovar(solicitacao, emprestimo);
		boolean rejeitar = rejeitar(solicitacao, emprestimo);

		if (aprovar) {
			emprestimo.setDataHoraAprovacao(FlexibleCalendar.currentDateTime());
			emprestimo.setStatus(StatusEmprestimo.APROVADO);

			emprestimo.getTotalAPagar();

		} else if (rejeitar) {
			emprestimo.setStatus(StatusEmprestimo.REJEITADO);
			emprestimo.setDataHoraRejeicao(FlexibleCalendar.currentDateTime());

		}
		else {
			emprestimo.setStatus(StatusEmprestimo.EM_ANALISE);	
		}
		
		repository.save(emprestimo);
		return emprestimo;
	}

	private boolean aprovar(SolicitacaoEmprestimoDTO solicitacao, Emprestimo emprestimo) {
		Parcela parcela = emprestimo.getParcelas().get(0);
		if (emprestimo.getCliente().getLimiteCredito().compareTo(solicitacao.getValorSolicitado()) >= 0) {
			emprestimo.setObservacao("LIMITE DE CRÉDITO ATENDE AO VALOR SOLICITADO");
			return true;
		} else if (emprestimo.getCliente().getRendaMensal()
				.compareTo(parcela.getValorTotal().multiply(new BigDecimal(3))) > 0) {
			emprestimo.setObservacao("RENDA MENSAL DO CLIENTE 3X SUPERIOR A PARCELA");
			return true;
		}
		return false;
	}

	private boolean rejeitar(SolicitacaoEmprestimoDTO solicitacao, Emprestimo emprestimo) {

		if (emprestimo.getCliente().getLimiteCredito().compareTo(solicitacao.getValorSolicitado()) < 0) {

			emprestimo.setObservacao("LIMITE DE CRÉDITO NÃO ATENDE O VALOR SOLICITADO");
		}
		return true;
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
