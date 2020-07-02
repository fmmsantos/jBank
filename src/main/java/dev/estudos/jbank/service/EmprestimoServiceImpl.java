package dev.estudos.jbank.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;

import dev.estudos.jbank.dto.SolicitacaoEmprestimoTDO;
import dev.estudos.jbank.model.Emprestimo;
import dev.estudos.jbank.model.StatusEmprestimo;
import dev.estudos.jbank.repository.EmprestimoRepository;

public class EmprestimoServiceImpl implements EmprestimoService {

	@Autowired
	private EmprestimoRepository repository;

	@Override
	public Emprestimo simular(SolicitacaoEmprestimoTDO solicitacao) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Emprestimo contratar(SolicitacaoEmprestimoTDO solicitacao) {

		if (solicitacao != null) {
			solicitacao.getCpfCnpjCliente();
			solicitacao.getValorSolicitado();
			solicitacao.getQtdParcelas();
			solicitacao.getDataHoraSolicitado();
		}

		if (solicitacao.getCpfCnpjCliente() == null) {
			throw new IllegalArgumentException("Cliente não Registrado. Porfavor, realizar o cadastramento");
		}
		Emprestimo emprestimo = new Emprestimo();
		if (emprestimo != null) {

			emprestimo.getCliente().setCpfCnpj(solicitacao.getCpfCnpjCliente());
			emprestimo.setValorSolicitado(solicitacao.getValorSolicitado());
			emprestimo.setTaxaJurosAoMes(emprestimo.getCliente().getTaxaJurosAoMes());
			emprestimo.setTotalJuros(emprestimo.getValorSolicitado().multiply(emprestimo.getTaxaJurosAoMes())
					.multiply(new BigDecimal(solicitacao.getQtdParcelas())));
			emprestimo.setTotalAPagar(solicitacao.getValorSolicitado().add(emprestimo.getTotalJuros()));
			boolean aprovado = aprovar(solicitacao);
			boolean rejeitado = rejeitar(solicitacao);

			if(aprovado) {
				emprestimo.setStatusEmprestimo(StatusEmprestimo.APROVADO);
				
			}
			else if(rejeitado) {
				emprestimo.setStatusEmprestimo(StatusEmprestimo.REJEITADO);
				
			}
		}
		return emprestimo;
	}

	@Override
	public boolean aprovar(SolicitacaoEmprestimoTDO solicitacao) {
		Emprestimo emprestimo = null;
		if (emprestimo.getCliente().getLimiteCredito().compareTo(solicitacao.getValorSolicitado())>=0) {
			emprestimo.setObservacao("LIMITE DE CRÉDITO ATENDE AO VALOR SOLICITADO");
			return true;
		} else if (emprestimo.getCliente().getRendaMesal().compareTo(emprestimo.getParcela().getValorPrincipal().multiply(new BigDecimal("3"))) >0) {
			emprestimo.setObservacao("RENDA MENSAL DO CLIENTE 3X SUPERIOR A PARCELA");
			return true;
		}
		return true;
	}

	@Override
	public boolean rejeitar(SolicitacaoEmprestimoTDO solicitacao) {
		Emprestimo emprestimo = null;
		if (emprestimo.getCliente().getLimiteCredito().compareTo(solicitacao.getValorSolicitado())<0) {
			emprestimo.setObservacao("LIMITE DE CRÉDITO NÃO ATENDE O VALOR SOLICITADO");
			return false;
		}
		return false;
	}

}
