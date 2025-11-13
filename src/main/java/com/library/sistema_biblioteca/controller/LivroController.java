package com.library.sistema_biblioteca.controller;

import com.library.sistema_biblioteca.model.Livro;
import com.library.sistema_biblioteca.repository.LivroRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/livros")
@Tag(name = "Livros", description = "API para gerenciamento de livros")
public class LivroController {

    @Autowired
    private LivroRepository livroRepository;

    @Operation(
        summary = "Criar novo livro",
        description = "Cria um novo livro no sistema com as informações fornecidas"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Livro criado com sucesso",
            content = @Content(schema = @Schema(implementation = Livro.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos fornecidos"
        )
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    public ResponseEntity<Livro> criar(
            @Parameter(description = "Dados do livro a ser criado", required = true)
            @Valid @RequestBody Livro livro) {
        Livro novoLivro = livroRepository.save(livro);
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novoLivro.getId())
                .toUri();
        
        return ResponseEntity.created(location).body(novoLivro);
    }

    @Operation(
        summary = "Listar todos os livros",
        description = "Retorna uma lista com todos os livros cadastrados no sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de livros retornada com sucesso",
            content = @Content(schema = @Schema(implementation = Livro.class))
        )
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    public ResponseEntity<List<Livro>> listarTodos() {
        List<Livro> livros = livroRepository.findAll();
        return ResponseEntity.ok(livros);
    }

    @Operation(
        summary = "Buscar livro por ID",
        description = "Retorna um livro específico pelo seu ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Livro encontrado",
            content = @Content(schema = @Schema(implementation = Livro.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Livro não encontrado"
        )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    public ResponseEntity<Livro> buscarPorId(
            @Parameter(description = "ID do livro", required = true, example = "1")
            @PathVariable Long id) {
        return livroRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Atualizar livro",
        description = "Atualiza as informações de um livro existente"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Livro atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = Livro.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Livro não encontrado"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos fornecidos"
        )
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    public ResponseEntity<Livro> atualizar(
            @Parameter(description = "ID do livro", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Dados atualizados do livro", required = true)
            @Valid @RequestBody Livro livroAtualizado) {
        return livroRepository.findById(id)
                .map(livro -> {
                    livro.setTitulo(livroAtualizado.getTitulo());
                    livro.setIsbn(livroAtualizado.getIsbn());
                    livro.setAnoPublicacao(livroAtualizado.getAnoPublicacao());
                    livro.setGenero(livroAtualizado.getGenero());
                    livro.setQuantidadeTotal(livroAtualizado.getQuantidadeTotal());
                    livro.setQuantidadeDisponivel(livroAtualizado.getQuantidadeDisponivel());
                    livro.setAutor(livroAtualizado.getAutor());
                    return ResponseEntity.ok(livroRepository.save(livro));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Deletar livro",
        description = "Remove um livro do sistema pelo seu ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Livro deletado com sucesso"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Livro não encontrado"
        )
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do livro", required = true, example = "1")
            @PathVariable Long id) {
        if (livroRepository.existsById(id)) {
            livroRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
