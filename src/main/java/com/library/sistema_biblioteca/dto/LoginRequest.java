package com.library.sistema_biblioteca.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Usuário é obrigatório")
    private String usuario;

    @NotBlank(message = "Senha é obrigatória")
    private String senha;
}
