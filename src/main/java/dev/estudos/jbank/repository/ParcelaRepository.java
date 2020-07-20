package dev.estudos.jbank.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import dev.estudos.jbank.model.Parcela;
@Repository
public interface ParcelaRepository extends CrudRepository<Parcela, Long>{
	
	
	// esse aqui eh o mais correto
	Parcela findByEmprestimoIdAndNumero(Long idEmprestimo, Integer numParcela);

}
