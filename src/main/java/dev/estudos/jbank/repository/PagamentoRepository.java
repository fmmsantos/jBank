package dev.estudos.jbank.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import dev.estudos.jbank.model.PagamentoParcela;
import dev.estudos.jbank.model.Parcela;

@Repository
public interface PagamentoRepository extends CrudRepository<PagamentoParcela, Long>{
	
	


}
