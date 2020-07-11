package dev.estudos.jbank.emprestimo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import dev.digytal.ymltest.YmlTestCase;
import dev.digytal.ymltest.YmlTestCaseLoader;
import dev.estudos.jbank.model.Emprestimo;
import dev.estudos.jbank.model.StatusEmprestimo;
import dev.estudos.jbank.repository.EmprestimoRepository;
import dev.estudos.jbank.service.EmprestimoService;

@SpringBootTest
public class AprovarEmprestimoTests {
	
	@Autowired
	YmlTestCaseLoader loader;
	
	@Autowired
	EmprestimoService emprestimoService;
	
	@Autowired
	EmprestimoRepository emprestimoRepository;

	private YmlTestCase testCase;
	
	@Test
	public void given_emprestimoEmAnalise_then_deveAprovar() {
		execTestCase();
		
		Long idEmprestimo = testCase.getInputArgs().getLong("idEmprestimo");
		String motivo = testCase.getInputArgs().getString("motivo");
		
		Optional<Emprestimo> found = emprestimoRepository.findById(idEmprestimo);
		
		assertTrue(found.isPresent(), "O emprestimo deveria existir");
		assertEquals(StatusEmprestimo.APROVADO, found.get().getStatus(), "O emprestimo deveria estar com o status APROVADO");
		assertEquals(motivo, found.get().getObservacao(), "O emprestimo deveria estar com a observação igual ao motivo informado na aprovação");
	}
	
	@Test
	public void given_emprestimoJaAprovado_then_deveEmitirErro() {
		execTestCase();
	}
	
	@Test
	public void given_emprestimoJaRejeitado_then_deveEmitirErro() {
		execTestCase();
	}
	
	@Test
	public void given_idEmprestimoInexistente_then_deveEmitirErro() {
		execTestCase();
	}
	
	@Test
	public void given_parametrosInvalidos_then_deveEmitirErro() {
		execTestCase();
	}
	
	@Test
	public void given_motimoNaoInformado_then_deveEmitirErro() {
		execTestCase();
	}

	private void execTestCase() {
		testCase = loader.loadOfMethodName();
		
		testCase.processWithArgs(args -> {
			Long idEmprestimo = args.getLong("idEmprestimo");
			String motivo = args.getString("motivo");
			
			return emprestimoService.aprovar(idEmprestimo, motivo);
		});
	}
	
}
