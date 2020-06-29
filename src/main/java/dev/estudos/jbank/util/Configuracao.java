package dev.estudos.jbank.util;

import java.math.BigDecimal;

public class Configuracao {
	private Long id;
	private BigDecimal taxaJurosPadrao;
	private Integer quantMaxParcelas;
	private BigDecimal valorMinParcela;
	
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
	public Integer getQuantMaxParcelas() {
		return quantMaxParcelas;
	}
	public void setQuantMaxParcelas(Integer quantMaxParcelas) {
		this.quantMaxParcelas = quantMaxParcelas;
	}
	public BigDecimal getValorMinParcela() {
		return valorMinParcela;
	}
	public void setValorMinParcela(BigDecimal valorMinParcela) {
		this.valorMinParcela = valorMinParcela;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Configuracao other = (Configuracao) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	

}
