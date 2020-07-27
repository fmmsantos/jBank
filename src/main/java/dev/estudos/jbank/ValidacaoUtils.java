package dev.estudos.jbank;

public class ValidacaoUtils {
	public static boolean somenteNumeros(String texto){
		return texto.matches("\\d*");
	}
	public static boolean numeroDocumento(String texto){
		return texto.matches("\\d*\\/\\d*");
	}
	public static void main(String[] args) {
		System.out.println(numeroDocumento("123/045"));
	}
}
