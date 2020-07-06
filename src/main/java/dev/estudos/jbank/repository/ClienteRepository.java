package dev.estudos.jbank.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import dev.estudos.jbank.model.Cliente;
import dev.estudos.jbank.model.Emprestimo;


@Repository
public interface ClienteRepository extends CrudRepository<Cliente, Long> {
	Cliente findBycpfCnpj(String cpfCnpj);
}

