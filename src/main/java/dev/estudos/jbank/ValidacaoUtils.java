package dev.estudos.jbank;

public class ValidacaoUtils {
	public static boolean somenteNumeros(String texto){
		return texto.matches("\\d*");
	}
}
