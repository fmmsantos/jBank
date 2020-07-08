package dev.estudos.jbank.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import dev.estudos.jbank.dto.SolicitacaoEmprestimoDTO;
import dev.estudos.jbank.model.Cliente;
import dev.estudos.jbank.model.Emprestimo;
import dev.estudos.jbank.model.Parcela;
import dev.estudos.jbank.utils.FlexibleCalendar;

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
	
	SolicitacaoEmprestimoDTO solicitacao = new SolicitacaoEmprestimoDTO();
	solicitacao.setCpfCnpjCliente(cliente.getCpfCnpj());
	solicitacao.setQtdParcelas(3);
	solicitacao.setValorSolicitado(new BigDecimal("600"));
	
	Emprestimo emprestimo = new Emprestimo();
	List<Parcela> parcelas = new ArrayList<>();
	Parcela parcela = new Parcela();
	parcela.setDataVencimento(LocalDate.now());
	parcela.setNumero(1);
	parcela.setValorJuros(new BigDecimal("162.50"));
	parcela.setValorPrincipal(new BigDecimal("2166.67"));
	parcela.setValorTotal(new BigDecimal("2329.17"));
	//BigDecimal parc = total.divide(numeroParcela);
	for(int i=0;i<solicitacao.getQtdParcelas().intValue();i++) {
	parcela.setValorJuros(emprestimo.getTotalJuros().divide(new BigDecimal(solicitacao.getQtdParcelas()),2));
		
	}
	
	System.out.println(parcela.getValorJuros());
	

	
	}

}
