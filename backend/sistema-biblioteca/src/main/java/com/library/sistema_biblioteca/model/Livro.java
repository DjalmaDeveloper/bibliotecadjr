package com.library.sistema_biblioteca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "livros")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Título é obrigatório")
    @Column(nullable = false)
    private String titulo;

    @Column(unique = true, length = 20)
    private String isbn;

    @Column(name = "ano_publicacao")
    private Integer anoPublicacao;

    private String genero;

    @Min(value = 0, message = "Quantidade total não pode ser negativa")
    @Column(name = "quantidade_total")
    private Integer quantidadeTotal = 0;

    @Min(value = 0, message = "Quantidade disponível não pode ser negativa")
    @Column(name = "quantidade_disponivel")
    private Integer quantidadeDisponivel = 0;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "autor_id")
    private Autor autor;
}
