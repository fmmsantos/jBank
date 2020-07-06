package dev.estudos.jbank.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.estudos.jbank.dto.SolicitacaoEmprestimoTDO;
import dev.estudos.jbank.model.Cliente;
import dev.estudos.jbank.model.Emprestimo;
import dev.estudos.jbank.model.StatusEmprestimo;
import dev.estudos.jbank.repository.ClienteRepository;
import dev.estudos.jbank.repository.EmprestimoRepository;

@Service
public class EmprestimoServiceImpl implements EmprestimoService {

	@Autowired
	private EmprestimoRepository repository;
	@Autowired
	private ClienteRepository repoCliente;

	@Override
	public Emprestimo simular(SolicitacaoEmprestimoTDO solicitacao) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Emprestimo contratar(SolicitacaoEmprestimoTDO solicitacao) {

		if (solicitacao.getCpfCnpjCliente() == null) {
			throw new IllegalArgumentException("Cliente não Registrado. Porfavor, realizar o cadastramento");
		}
		
		Emprestimo emprestimo = new Emprestimo();
		
		
		if (solicitacao!= null) {
			
			Cliente cliente = repoCliente.findBycpfCnpj(solicitacao.getCpfCnpjCliente());
			if(cliente==null)
				throw new IllegalArgumentException("Cliente não localizado com este cpf " + solicitacao.getCpfCnpjCliente());
			
			emprestimo.setCliente(cliente);
			emprestimo.setTaxaJurosAoMes(cliente.getTaxaJurosAoMes());
			
			emprestimo.setValorSolicitado(solicitacao.getValorSolicitado());
			
			
			emprestimo.setTotalJuros(emprestimo.getValorSolicitado().multiply(emprestimo.getTaxaJurosAoMes())
					.multiply(new BigDecimal(solicitacao.getQtdParcelas())));
			emprestimo.setTotalAPagar(solicitacao.getValorSolicitado().add(emprestimo.getTotalJuros()));
			
			emprestimo.setDataHoraSolicitacao(LocalDateTime.now());
						
			

		
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
