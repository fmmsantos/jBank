package dev.estudos.jbank.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import dev.estudos.jbank.model.Emprestimo;
import dev.estudos.jbank.service.EmprestimoService;

public class SolicitacaoEmprestimoTDO {
	private String cpfCnpjCliente;
	private BigDecimal valorSolicitado;
	private Integer qtdParcelas;
	private LocalDateTime dataHoraSolicitado;
	
	
	public String getCpfCnpjCliente() {
		return cpfCnpjCliente;
	}
	public void setCpfCnpjCliente(String cpfCnpjCliente) {
		this.cpfCnpjCliente = cpfCnpjCliente;
	}
	public BigDecimal getValorSolicitado() {
		return valorSolicitado;
	}
	public void setValorSolicitado(BigDecimal valorSolicitado) {
		this.valorSolicitado = valorSolicitado;
	}
	public Integer getQtdParcelas() {
		return qtdParcelas;
	}
	public void setQtdParcelas(Integer qtdParcelas) {
		this.qtdParcelas = qtdParcelas;
	}
	public LocalDateTime getDataHoraSolicitado() {
		return dataHoraSolicitado;
	}
	public void setDataHoraSolicitado(LocalDateTime dataHoraSolicitado) {
		this.dataHoraSolicitado = dataHoraSolicitado;
	}
	
	
}