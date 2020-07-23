package dev.estudos.jbank.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

import dev.estudos.jbank.model.Configuracao;
import dev.estudos.jbank.utils.FlexibleCalendar;

public class Teste {
	public static void main(String[] args) {
	
		Configuracao config = new Configuracao();
		
		config.setJurosDeMora(new BigDecimal(1));
		BigDecimal mes = new BigDecimal(30);
		BigDecimal dividir = config.getJurosDeMora().divide(new BigDecimal(30),3,RoundingMode.HALF_EVEN);
		
		LocalDate vencimento = LocalDate.of(2020, 07, 10);
		LocalDate hoje = LocalDate.now();
		
		int hoje1 = hoje.getDayOfMonth();
		int dia = vencimento.getDayOfMonth();
		int diasDeAtraso = hoje1-dia; 
		
		//System.out.println(diasDeAtraso);
		
		//System.out.println(dividir);
		
		BigDecimal diasVencid = dividir.multiply(new BigDecimal(diasDeAtraso));
		
		BigDecimal arredondar = diasVencid.round(new MathContext(2));
		BigDecimal arredondar1 = arredondar.divide(new BigDecimal(100));
		BigDecimal total= arredondar1.multiply(new BigDecimal(500));
		
	System.out.println(total);
		
		
		
		
		
		
		
		
	}

}
