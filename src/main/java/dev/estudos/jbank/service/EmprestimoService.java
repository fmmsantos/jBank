package dev.estudos.jbank.service;

import dev.estudos.jbank.dto.SolicitacaoEmprestimoDTO;
import dev.estudos.jbank.model.Emprestimo;
import dev.estudos.jbank.model.Parcela;

public interface EmprestimoService {
	Emprestimo simular(SolicitacaoEmprestimoDTO solicitacao);
	Emprestimo contratar (SolicitacaoEmprestimoDTO solicitacao);
	boolean aprovar(Long idEmprestimo, String motivo);
	boolean rejeitar(Long idEmprestimo, String motivo);
	Parcela processarStatusParcela(Long idEmprestimo, int numParcela);
	Parcela getParcela(Long idEmprestimo, int numParcela);
	

}
