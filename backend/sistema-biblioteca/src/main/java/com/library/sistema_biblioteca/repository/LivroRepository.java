package com.library.sistema_biblioteca.repository;

import com.library.sistema_biblioteca.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LivroRepository extends JpaRepository<Livro, Long> {
    // JpaRepository já fornece métodos: save, findById, findAll, delete, etc.
}
