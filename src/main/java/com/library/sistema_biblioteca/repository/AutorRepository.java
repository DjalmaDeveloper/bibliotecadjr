package com.library.sistema_biblioteca.repository;

import com.library.sistema_biblioteca.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    // JpaRepository já fornece métodos: save, findById, findAll, delete, etc.
}
