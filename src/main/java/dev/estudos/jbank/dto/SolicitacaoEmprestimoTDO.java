package dev.estudos.jbank.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import dev.estudos.jbank.model.Emprestimo;
import dev.estudos.jbank.service.EmprestimoService;

public class SolicitacaoEmprestimoTDO implements EmprestimoService {
	private String cpfCnpjCliente;
	private BigDecimal valorSolicitado;
	private Integer qtdParcelas;
	private LocalDateTime dataHoraSolicitado;
	
	
	@Override
	public Emprestimo simular(SolicitacaoEmprestimoTDO solicitacao) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Emprestimo contratar(SolicitacaoEmprestimoTDO solicitacao) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean aprovar(SolicitacaoEmprestimoTDO solicitacao) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean rejeitar(SolicitacaoEmprestimoTDO solicitacao) {
		// TODO Auto-generated method stub
		return false;
	}

}
