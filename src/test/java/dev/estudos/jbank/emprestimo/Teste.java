package dev.estudos.jbank.emprestimo;

import java.time.LocalDate;

import dev.estudos.jbank.utils.FlexibleCalendar;

public class Teste {

	public static void main(String[] args) {
		LocalDate data = LocalDate.of(2019, 07, 24);
		
		boolean comp = data.isBefore(FlexibleCalendar.currentDate());
		
		System.out.println(data.getDayOfMonth());

	}

}
