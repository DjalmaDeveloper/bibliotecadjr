package com.library.sistema_biblioteca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "emprestimos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "livro_id", nullable = false)
    @NotNull(message = "Livro é obrigatório")
    private Livro livro;

    @NotBlank(message = "Nome do usuário é obrigatório")
    @Column(name = "nome_usuario", nullable = false)
    private String nomeUsuario;

    @Email(message = "Email inválido")
    @NotBlank(message = "Email é obrigatório")
    @Column(name = "email_usuario", nullable = false)
    private String emailUsuario;

    @NotNull(message = "Data de empréstimo é obrigatória")
    @Column(name = "data_emprestimo", nullable = false)
    private LocalDate dataEmprestimo;

    @NotNull(message = "Data de devolução prevista é obrigatória")
    @Column(name = "data_devolucao_prevista", nullable = false)
    private LocalDate dataDevolucaoPrevista;

    @Column(name = "data_devolucao_real")
    private LocalDate dataDevolucaoReal;

    @Column(length = 50)
    private String status = "ATIVO";
}
