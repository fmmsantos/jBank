package dev.estudos.jbank.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Parcela {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Integer numero;
	private LocalDate dataVencimento;
	private BigDecimal valorPrincipal;
	private BigDecimal valorJuros;
	private BigDecimal valorTotal;
	
	
	public Integer getNumero() {
		return numero;
	}
	public void setNumero(Integer numero) {
		this.numero = numero;
	}
	public LocalDate getDataVencimento() {
		return dataVencimento;
	}
	public void setDataVencimento(LocalDate dataVencimento) {
		this.dataVencimento = dataVencimento;
	}
	public BigDecimal getValorPrincipal() {
		return valorPrincipal;
	}
	public void setValorPrincipal(BigDecimal valorPrincipal) {
		this.valorPrincipal = valorPrincipal;
	}
	public BigDecimal getValorJuros() {
		return valorJuros;
	}
	public void setValorJuros(BigDecimal valorJuros) {
		this.valorJuros = valorJuros;
	}
	public BigDecimal getValorTotal() {
		return valorTotal;
	}
	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}
	
	

}
