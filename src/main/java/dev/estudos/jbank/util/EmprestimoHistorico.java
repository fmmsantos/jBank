package dev.estudos.jbank.util;

import java.time.LocalDateTime;

import dev.estudos.jbank.model.Emprestimo;
import dev.estudos.jbank.model.StatusEmprestimo;

public class EmprestimoHistorico {
	private LocalDateTime dataHora;
	private String observacao;
	private StatusEmprestimo statusEmprestimo;
	private Emprestimo emprestimo;
	
	
	
	public StatusEmprestimo getStatusEmprestimo() {
		return statusEmprestimo;
	}
	public Emprestimo getEmprestimo() {
		return emprestimo;
	}
	public void setEmprestimo(Emprestimo emprestimo) {
		this.emprestimo = emprestimo;
	}
	public void setStatusEmprestimo(StatusEmprestimo statusEmprestimo) {
		this.statusEmprestimo = statusEmprestimo;
	}
	public LocalDateTime getDataHora() {
		return dataHora;
	}
	public void setDataHora(LocalDateTime dataHora) {
		this.dataHora = dataHora;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	
	

}
