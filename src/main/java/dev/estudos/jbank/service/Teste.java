package dev.estudos.jbank.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import dev.estudos.jbank.dto.SolicitacaoEmprestimoDTO;
import dev.estudos.jbank.model.Cliente;
import dev.estudos.jbank.model.Emprestimo;

public class Teste {
	public static void main(String[] args) {
		EmprestimoService emprestimoService = new EmprestimoServiceImpl();	
	Cliente cliente = new Cliente();
	cliente.setId(1L);
	cliente.setCpfCnpj("02769955381");
	cliente.setLimiteCredito(new BigDecimal("1.000"));
	cliente.setNome("fabiana santos");
	cliente.setRendaMesal(new BigDecimal("1.500"));
	cliente.setTaxaJurosAoMes(new BigDecimal("0.02"));
	
	SolicitacaoEmprestimoDTO dto = new SolicitacaoEmprestimoDTO();
	dto.setCpfCnpjCliente(cliente.getCpfCnpj());
	dto.setQtdParcelas(3);
	dto.setValorSolicitado(new BigDecimal("600"));

	
	Emprestimo result = emprestimoService.contratar(dto);
	System.out.println(result!=null);
	System.out.println(result.getStatusEmprestimo());
	}

}
