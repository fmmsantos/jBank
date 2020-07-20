package dev.estudos.jbank.service;

import java.math.BigDecimal;

import dev.estudos.jbank.model.PagamentoParcela;

public interface PagamentoParcelaService {
	PagamentoParcela pagar(String numeroDocumento, BigDecimal valorPago);

}
