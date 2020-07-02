package dev.estudos.jbank.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import dev.estudos.jbank.model.Emprestimo;


@Repository
public interface ClienteRepository extends CrudRepository<Emprestimo, Long> {

}

