package dev.estudos.jbank.model;

import java.time.LocalDateTime;

import javax.persistence.ManyToOne;

public class EmprestimoHistorico {
	
	public LocalDateTime dataHora;
	public String observacao;
	
	public Emprestimo emprestimo;

}
