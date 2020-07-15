package dev.estudos.jbank.model;

import java.math.BigDecimal;

import javax.annotation.Generated;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Configuracao {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private BigDecimal taxaJurosPadrao = BigDecimal.ZERO;
	private Integer qtdMaximaParcelas= 0;
	private BigDecimal valorMinimoParcela = BigDecimal.ZERO;
	public int idadeMinima=0;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public BigDecimal getTaxaJurosPadrao() {
		return taxaJurosPadrao;
	}
	public void setTaxaJurosPadrao(BigDecimal taxaJurosPadrao) {
		this.taxaJurosPadrao = taxaJurosPadrao;
	}
	public Integer getQtdMaximaParcelas() {
		return qtdMaximaParcelas;
	}
	public void setQtdMaximaParcelas(Integer qtdMaximaParcelas) {
		this.qtdMaximaParcelas = qtdMaximaParcelas;
	}
	public BigDecimal getValorMinimoParcela() {
		return valorMinimoParcela;
	}
	public void setValorMinimoParcela(BigDecimal valorMinimoParcela) {
		this.valorMinimoParcela = valorMinimoParcela;
	}
	

}
