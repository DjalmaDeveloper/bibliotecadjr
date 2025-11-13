package com.library.sistema_biblioteca.controller;

import com.library.sistema_biblioteca.model.Emprestimo;
import com.library.sistema_biblioteca.repository.EmprestimoRepository;
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
@RequestMapping("/api/emprestimos")
@Tag(name = "Empréstimos", description = "API para gerenciamento de empréstimos")
@PreAuthorize("hasRole('ADMIN')")
public class EmprestimoController {

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Operation(
        summary = "Criar novo empréstimo",
        description = "Registra um novo empréstimo de livro no sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Empréstimo criado com sucesso",
            content = @Content(schema = @Schema(implementation = Emprestimo.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos fornecidos"
        )
    })
    @PostMapping
    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    public ResponseEntity<Emprestimo> criar(
            @Parameter(description = "Dados do empréstimo a ser criado", required = true)
            @Valid @RequestBody Emprestimo emprestimo) {
        Emprestimo novoEmprestimo = emprestimoRepository.save(emprestimo);
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novoEmprestimo.getId())
                .toUri();
        
        return ResponseEntity.created(location).body(novoEmprestimo);
    }

    @Operation(
        summary = "Listar todos os empréstimos",
        description = "Retorna uma lista com todos os empréstimos cadastrados no sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de empréstimos retornada com sucesso",
            content = @Content(schema = @Schema(implementation = Emprestimo.class))
        )
    })
    @GetMapping
    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    public ResponseEntity<List<Emprestimo>> listarTodos() {
        List<Emprestimo> emprestimos = emprestimoRepository.findAll();
        return ResponseEntity.ok(emprestimos);
    }

    @Operation(
        summary = "Buscar empréstimo por ID",
        description = "Retorna um empréstimo específico pelo seu ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Empréstimo encontrado",
            content = @Content(schema = @Schema(implementation = Emprestimo.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Empréstimo não encontrado"
        )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    public ResponseEntity<Emprestimo> buscarPorId(
            @Parameter(description = "ID do empréstimo", required = true, example = "1")
            @PathVariable Long id) {
        return emprestimoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Atualizar empréstimo",
        description = "Atualiza as informações de um empréstimo existente (útil para registrar devolução)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Empréstimo atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = Emprestimo.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Empréstimo não encontrado"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos fornecidos"
        )
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    public ResponseEntity<Emprestimo> atualizar(
            @Parameter(description = "ID do empréstimo", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Dados atualizados do empréstimo", required = true)
            @Valid @RequestBody Emprestimo emprestimoAtualizado) {
        return emprestimoRepository.findById(id)
                .map(emprestimo -> {
                    emprestimo.setLivro(emprestimoAtualizado.getLivro());
                    emprestimo.setNomeUsuario(emprestimoAtualizado.getNomeUsuario());
                    emprestimo.setEmailUsuario(emprestimoAtualizado.getEmailUsuario());
                    emprestimo.setDataEmprestimo(emprestimoAtualizado.getDataEmprestimo());
                    emprestimo.setDataDevolucaoPrevista(emprestimoAtualizado.getDataDevolucaoPrevista());
                    emprestimo.setDataDevolucaoReal(emprestimoAtualizado.getDataDevolucaoReal());
                    emprestimo.setStatus(emprestimoAtualizado.getStatus());
                    return ResponseEntity.ok(emprestimoRepository.save(emprestimo));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Deletar empréstimo",
        description = "Remove um empréstimo do sistema pelo seu ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Empréstimo deletado com sucesso"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Empréstimo não encontrado"
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do empréstimo", required = true, example = "1")
            @PathVariable Long id) {
        if (emprestimoRepository.existsById(id)) {
            emprestimoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
