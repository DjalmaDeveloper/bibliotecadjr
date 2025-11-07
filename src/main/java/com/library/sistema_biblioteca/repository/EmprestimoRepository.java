package com.library.sistema_biblioteca.repository;

import com.library.sistema_biblioteca.model.Emprestimo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    // JpaRepository já fornece métodos: save, findById, findAll, delete, etc.
}
