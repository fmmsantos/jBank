package dev.estudos.jbank.model;

import java.time.LocalDate;

public class Teste {
	public static void main(String[] args) {
		LocalDate dataNascimento = LocalDate.of(1989, 7, 24);
		int idade = 0;
		
		Teste.calcularIdade(dataNascimento, idade);
	
	System.out.println(Teste.calcularIdade(dataNascimento,idade));
	}
	public static Integer calcularIdade(LocalDate dataNascimento, int idade) {
		LocalDate dataDate = LocalDate.from(dataNascimento);
		int hoje = LocalDate.now().getYear();
		 idade = Math.subtractExact(hoje, dataDate.getYear());
		
		return idade;
	}
}
