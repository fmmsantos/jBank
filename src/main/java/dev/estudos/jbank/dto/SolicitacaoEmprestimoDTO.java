package dev.estudos.jbank.dto;

import java.math.BigDecimal;

public class SolicitacaoEmprestimoDTO {
	private String cpfCnpjCliente;
	private BigDecimal valorSolicitado;
	private Integer qtdParcelas;
	
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
	
}