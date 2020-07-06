package dev.estudos.jbank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import dev.estudos.jbank.dto.SolicitacaoEmprestimoDTO;
import dev.estudos.jbank.model.Emprestimo;
import dev.estudos.jbank.service.EmprestimoService;

@SpringBootTest
class JBankApplicationTests {

	@Autowired
	EmprestimoService emprestimoService;
	
	@Test
	void contextLoads() {
	}
	
	@Test
	public void contratarEmprestimo() {
		SolicitacaoEmprestimoDTO solicitacao = new SolicitacaoEmprestimoDTO();
		
		solicitacao.setCpfCnpjCliente("65651227334");
		solicitacao.setValorSolicitado(new BigDecimal("1000.00"));
		solicitacao.setQtdParcelas(2);
		
		Emprestimo result = emprestimoService.contratar(solicitacao);
		
		assertNotNull(result);
		assertEquals(new BigDecimal("1000.00"), result.getValorSolicitado());		
	}

}
