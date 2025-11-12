package com.library.sistema_biblioteca.service;

import com.library.sistema_biblioteca.dto.AuthResponse;
import com.library.sistema_biblioteca.dto.LoginRequest;
import com.library.sistema_biblioteca.dto.RegisterRequest;
import com.library.sistema_biblioteca.model.Usuario;
import com.library.sistema_biblioteca.repository.UsuarioRepository;
import com.library.sistema_biblioteca.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        // Verificar se usuario já existe
        if (usuarioRepository.existsByUsuario(request.getUsuario())) {
            throw new RuntimeException("Usuario ja existe!");
        }

        // Verificar se email já existe
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email ja existe!");
        }

        // Criar novo usuário
        Usuario usuario = new Usuario();
        usuario.setUsuario(request.getUsuario());
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setRole(request.getRole() != null ? request.getRole() : "USER");
        usuario.setAtivo(true);

        usuarioRepository.save(usuario);

        // Gerar token
        String token = jwtUtil.generateToken(usuario);

        return new AuthResponse(
                token,
                usuario.getId(),
                usuario.getUsername(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole()
        );
    }

    public AuthResponse login(LoginRequest request) {
        // Autenticar
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsuario(),
                        request.getSenha()
                )
        );

        // Buscar usuário
        Usuario usuario = usuarioRepository.findByUsuario(request.getUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));

        // Gerar token
        String token = jwtUtil.generateToken(usuario);

        return new AuthResponse(
                token,
                usuario.getId(),
                usuario.getUsername(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole()
        );
    }
}
