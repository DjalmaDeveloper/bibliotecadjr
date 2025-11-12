package com.library.sistema_biblioteca.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Resposta com dados do usuário")
public class UsuarioResponse {

    @Schema(description = "ID único do usuário", example = "1")
    private Long id;

    @Schema(description = "Nome de usuário", example = "joao123")
    private String usuario;

    @Schema(description = "Nome completo do usuário", example = "João Silva")
    private String nome;

    @Schema(description = "E-mail do usuário", example = "joao@email.com")
    private String email;

    @Schema(description = "Perfil/Role do usuário", example = "USER", allowableValues = {"USER", "ADMIN"})
    private String perfil;

    @Schema(description = "Status do usuário", example = "Ativo")
    private String status;

    @Schema(description = "Data de criação do usuário", example = "2025-11-10T19:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataCriacao;
}

