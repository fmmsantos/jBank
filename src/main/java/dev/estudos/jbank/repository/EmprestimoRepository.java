package dev.estudos.jbank.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import dev.estudos.jbank.model.Emprestimo;
import dev.estudos.jbank.model.StatusEmprestimo;

@Repository
public interface EmprestimoRepository extends CrudRepository<Emprestimo, Long> {
	Emprestimo findByStatus(StatusEmprestimo status);
	
	

}
