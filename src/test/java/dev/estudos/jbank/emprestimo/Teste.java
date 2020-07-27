package dev.estudos.jbank.emprestimo;

import java.time.LocalDate;

import dev.estudos.jbank.utils.FlexibleCalendar;

public class Teste {

	public static void main(String[] args) {
		LocalDate data = LocalDate.of(2019, 07, 24);
		
		boolean comp = data.isBefore(FlexibleCalendar.currentDate());
		
	//	System.out.println(data.getDayOfMonth());

		
		String idade = "2A023/2";
		
		String a = "a2w5sd6545";
		
			System.out.println(!a.contains("\\d*"));
			System.out.println(idade.matches("\\d*") );
			System.out.println(!idade.contains("/"));
			System.out.println(idade.contains("/"));
		
		
	
		
	}

}
