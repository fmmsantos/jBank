package dev.estudos.jbank.model;

import java.time.LocalDate;

import net.bytebuddy.asm.Advice.Local;

public class Teste {
	public static void main(String[] args) {
		
	LocalDate dataNascimento = LocalDate.of(1989, 7, 24);
	
	Integer data = dataNascimento.getYear();
	
	System.out.println(Math.subtractExact(data, LocalDate.now().getYear()));
	
	}
}
