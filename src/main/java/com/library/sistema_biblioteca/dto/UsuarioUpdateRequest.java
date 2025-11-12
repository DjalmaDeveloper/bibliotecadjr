package com.library.sistema_biblioteca.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Requisição para atualização de dados do usuário")
public class UsuarioUpdateRequest {

    @Size(min = 3, max = 50, message = "Usuário deve ter entre 3 e 50 caracteres")
    @Schema(description = "Nome de usuário", example = "joao123")
    private String usuario;

    @Schema(description = "Nome completo do usuário", example = "João Silva")
    private String nome;

    @Email(message = "Email inválido")
    @Schema(description = "E-mail do usuário", example = "joao@email.com")
    private String email;

    @Schema(description = "Perfil/Role do usuário", example = "USER", allowableValues = {"USER", "ADMIN"})
    private String perfil;

    @Schema(description = "Status ativo do usuário", example = "true")
    private Boolean ativo;

    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    @Schema(description = "Nova senha (opcional)", example = "novaSenha123")
    private String senha;
}

