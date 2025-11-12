package com.library.sistema_biblioteca.repository;

import com.library.sistema_biblioteca.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsuario(String usuario);

    Optional<Usuario> findByEmail(String email);

    Boolean existsByUsuario(String usuario);

    Boolean existsByEmail(String email);
}
