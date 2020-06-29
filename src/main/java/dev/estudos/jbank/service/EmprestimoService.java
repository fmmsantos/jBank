package dev.estudos.jbank.service;

import dev.estudos.jbank.dto.SolicitacaoEmprestimoTDO;
import dev.estudos.jbank.model.Emprestimo;

public interface EmprestimoService {
	Emprestimo simular(SolicitacaoEmprestimoTDO solicitacao);
	Emprestimo contratar (SolicitacaoEmprestimoTDO solicitacao);
	boolean aprovar(SolicitacaoEmprestimoTDO solicitacao);
	boolean rejeitar(SolicitacaoEmprestimoTDO solicitacao);
	

}
