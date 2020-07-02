package dev.estudos.jbank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import dev.estudos.jbank.util.Parcela;
@Entity
public class Emprestimo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private BigDecimal valorSolicitado;
	private BigDecimal taxaJurosAoMes;
	private BigDecimal totalJuros;
	private BigDecimal totalAPagar;
	private String observacao;
	private LocalDateTime dataHoraSolicitacao;
	private LocalDateTime dataHoraAprovacao;
	private LocalDateTime dataHoraRejeicao;
	private StatusEmprestimo statusEmprestimo;
	private Parcela parcela;
	private Cliente cliente;
	
	
	
	
	public Parcela getParcela() {
		return parcela;
	}
	public void setParcela(Parcela parcela) {
		this.parcela = parcela;
	}
	public StatusEmprestimo getStatusEmprestimo() {
		return statusEmprestimo;
	}
	public void setStatusEmprestimo(StatusEmprestimo statusEmprestimo) {
		this.statusEmprestimo = statusEmprestimo;
	}
	
	
	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public BigDecimal getValorSolicitado() {
		return valorSolicitado;
	}
	public void setValorSolicitado(BigDecimal valorSolicitado) {
		this.valorSolicitado = valorSolicitado;
	}
	public BigDecimal getTaxaJurosAoMes() {
		return taxaJurosAoMes;
	}
	public void setTaxaJurosAoMes(BigDecimal taxaJurosAoMes) {
		this.taxaJurosAoMes = taxaJurosAoMes;
	}
	public BigDecimal getTotalJuros() {
		return totalJuros;
	}
	public void setTotalJuros(BigDecimal totalJuros) {
		this.totalJuros = totalJuros;
	}
	public BigDecimal getTotalAPagar() {
		return totalAPagar;
	}
	public void setTotalAPagar(BigDecimal totalAPagar) {
		this.totalAPagar = totalAPagar;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	public LocalDateTime getDataHoraSolicitacao() {
		return dataHoraSolicitacao;
	}
	public void setDataHoraSolicitacao(LocalDateTime dataHoraSolicitacao) {
		this.dataHoraSolicitacao = dataHoraSolicitacao;
	}
	public LocalDateTime getDataHoraAprovacao() {
		return dataHoraAprovacao;
	}
	public void setDataHoraAprovacao(LocalDateTime dataHoraAprovacao) {
		this.dataHoraAprovacao = dataHoraAprovacao;
	}
	public LocalDateTime getDataHoraRejeicao() {
		return dataHoraRejeicao;
	}
	public void setDataHoraRejeicao(LocalDateTime dataHoraRejeicao) {
		this.dataHoraRejeicao = dataHoraRejeicao;
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
		Emprestimo other = (Emprestimo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
}
