package dev.estudos.jbank.model;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

import java.util.List;

public class Teste {
	public static void main(String[] args) {

		Parcela parcela1 = new Parcela();
		parcela1.setDataVencimento(LocalDate.of(2020, 6, 10));
		parcela1.setNumero(1);
		parcela1.setStatusParcela(StatusParcela.EM_ATRASO);
		parcela1.setValorTotal(new BigDecimal(400));
		
		Parcela parcela2 = new Parcela();
		parcela2.setDataVencimento(LocalDate.of(2020, 7, 10));
		parcela2.setNumero(2);
		parcela2.setStatusParcela(StatusParcela.EM_ATRASO);
		parcela2.setValorTotal(new BigDecimal(400));
		
		
		Parcela parcela3 = new Parcela();
		parcela3.setDataVencimento(LocalDate.of(2020, 8, 10));
		parcela3.setNumero(3);
		parcela3.setStatusParcela(StatusParcela.A_VENCER);
		parcela3.setValorTotal(new BigDecimal(400));
		
		List<Parcela> parcelas = new ArrayList<>();
		parcelas.add(parcela1);
		parcelas.add(parcela2);
		parcelas.add(parcela3);
		
		Emprestimo emprestimo = new Emprestimo();
		emprestimo.setParcelas(parcelas);
		
		String numeroDocumento = "510/2";
		
		String parcelaIdEmprestimo[] = numeroDocumento.split("/");

		String e = parcelaIdEmprestimo[0];
		String parcelaNumero = parcelaIdEmprestimo[1];
		
		Long idEmprestimo = Long.parseLong(e);
		int numero= Integer.parseInt(parcelaNumero);
		BigDecimal pagamento = new BigDecimal("300");
		
		for(Parcela parc : parcelas) {
			if(parc.getNumero()==numero && parc.getStatusParcela() !=StatusParcela.PAGO) {
				
				System.out.println(numero);
					
			}
			if(parc.getValorTotal().compareTo(pagamento)<0) {
				break;
			}
				
		}
		
		
			
			
			
			
			
			
			
			 
	}
	
	
	
}
