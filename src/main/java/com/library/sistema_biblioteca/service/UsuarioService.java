package com.library.sistema_biblioteca.service;

import com.library.sistema_biblioteca.dto.UsuarioResponse;
import com.library.sistema_biblioteca.dto.UsuarioUpdateRequest;
import com.library.sistema_biblioteca.model.Usuario;
import com.library.sistema_biblioteca.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Buscar todos os usuários
     */
    public List<UsuarioResponse> findAll() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Buscar usuário por ID
     */
    public UsuarioResponse findById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado com id: " + id));
        return convertToResponse(usuario);
    }

    /**
     * Buscar usuário por nome de usuário
     */
    public UsuarioResponse findByUsuario(String usuario) {
        Usuario user = usuarioRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado: " + usuario));
        return convertToResponse(user);
    }

    /**
     * Atualizar usuário
     */
    @Transactional
    public UsuarioResponse update(Long id, UsuarioUpdateRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado com id: " + id));

        // Atualizar campos se fornecidos
        if (request.getUsuario() != null && !request.getUsuario().isEmpty()) {
            // Verificar se o novo usuário já existe (exceto o próprio)
            usuarioRepository.findByUsuario(request.getUsuario())
                    .ifPresent(u -> {
                        if (!u.getId().equals(id)) {
                            throw new RuntimeException("Usuario ja existe!");
                        }
                    });
            usuario.setUsuario(request.getUsuario());
        }

        if (request.getNome() != null && !request.getNome().isEmpty()) {
            usuario.setNome(request.getNome());
        }

        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            // Verificar se o novo email já existe (exceto o próprio)
            usuarioRepository.findByEmail(request.getEmail())
                    .ifPresent(u -> {
                        if (!u.getId().equals(id)) {
                            throw new RuntimeException("Email ja existe!");
                        }
                    });
            usuario.setEmail(request.getEmail());
        }

        if (request.getPerfil() != null && !request.getPerfil().isEmpty()) {
            usuario.setRole(request.getPerfil());
        }

        if (request.getAtivo() != null) {
            usuario.setAtivo(request.getAtivo());
        }

        if (request.getSenha() != null && !request.getSenha().isEmpty()) {
            usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        }

        Usuario updated = usuarioRepository.save(usuario);
        return convertToResponse(updated);
    }

    /**
     * Deletar usuário
     */
    @Transactional
    public void delete(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario nao encontrado com id: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    /**
     * Alternar status do usuário (ativar/desativar)
     */
    @Transactional
    public UsuarioResponse toggleStatus(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado com id: " + id));
        
        usuario.setAtivo(!usuario.getAtivo());
        Usuario updated = usuarioRepository.save(usuario);
        return convertToResponse(updated);
    }

    /**
     * Converter entidade Usuario para UsuarioResponse
     */
    private UsuarioResponse convertToResponse(Usuario usuario) {
        UsuarioResponse response = new UsuarioResponse();
        response.setId(usuario.getId());
        response.setUsuario(usuario.getUsuario());
        response.setNome(usuario.getNome());
        response.setEmail(usuario.getEmail());
        response.setPerfil(usuario.getRole());
        response.setStatus(usuario.getAtivo() ? "Ativo" : "Inativo");
        response.setDataCriacao(usuario.getDataCriacao());
        return response;
    }
}

