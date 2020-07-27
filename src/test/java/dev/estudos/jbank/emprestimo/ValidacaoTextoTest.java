package dev.estudos.jbank.emprestimo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import dev.estudos.jbank.ValidacaoUtils;

public class ValidacaoTextoTest {
	@Test
	public void somenteNumeroTest() {
		String parcela = "12/100";
		boolean somenteNumero= ValidacaoUtils.somenteNumeros(parcela);
		Assert.isTrue(!somenteNumero);
	}
	@Test
	public void numeroETextoTest() {
		boolean somenteNumero=true;
		String parcela = "12A";
		somenteNumero= ValidacaoUtils.somenteNumeros(parcela);
		Assert.isTrue(!somenteNumero);
	}
	@Test
	public void numeroECaractETextoTest() {
		boolean somenteNumero=true;
		String parcela = "12A/2";
		somenteNumero= ValidacaoUtils.somenteNumeros(parcela);
		Assert.isTrue(!somenteNumero);
	}
	
	
}
