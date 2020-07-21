package dev.estudos.jbank.service;

import java.time.LocalDateTime;

import dev.estudos.jbank.utils.FlexibleCalendar;

public class Teste {
	public static void main(String[] args) {
		LocalDateTime dataHora = FlexibleCalendar.currentDateTime();
		
		System.out.println(dataHora);
	}

}
