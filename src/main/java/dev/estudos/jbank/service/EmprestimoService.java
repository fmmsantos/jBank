package dev.estudos.jbank.service;

import dev.estudos.jbank.dto.SolicitacaoEmprestimoDTO;
import dev.estudos.jbank.model.Emprestimo;

public interface EmprestimoService {
	Emprestimo simular(SolicitacaoEmprestimoDTO solicitacao);
	Emprestimo contratar (SolicitacaoEmprestimoDTO solicitacao);
	boolean aprovar(Long idEmprestimo, String motivo);
	boolean rejeitar(Long idEmprestimo, String motivo);
	

}
