package dev.estudos.jbank.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import dev.estudos.jbank.model.Configuracao;

public interface ConfiguracaoRepository extends CrudRepository<Configuracao, Long>{

	
	@Query("SELECT c FROM Configuracao c WHERE c.id = 1")
	Configuracao getConfiguracao();
	
}
