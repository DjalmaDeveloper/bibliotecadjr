package com.library.sistema_biblioteca.controller;

import com.library.sistema_biblioteca.model.Autor;
import com.library.sistema_biblioteca.repository.AutorRepository;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/autores")
@CrossOrigin(origins = {"https://bibliotecadjr.pages.dev/", "http://localhost:3000"})
@Tag(name = "Autores", description = "API para gerenciamento de autores")
public class AutorController {

    @Autowired
    private AutorRepository autorRepository;

    @Operation(
        summary = "Criar novo autor",
        description = "Cria um novo autor no sistema com as informações fornecidas"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Autor criado com sucesso",
            content = @Content(schema = @Schema(implementation = Autor.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos fornecidos"
        )
    })
    @PostMapping
    public ResponseEntity<Autor> criar(
            @Parameter(description = "Dados do autor a ser criado", required = true)
            @Valid @RequestBody Autor autor) {
        Autor novoAutor = autorRepository.save(autor);
        
        // Criar URI do recurso criado
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novoAutor.getId())
                .toUri();
        
        return ResponseEntity.created(location).body(novoAutor);
    }

    @Operation(
        summary = "Listar todos os autores",
        description = "Retorna uma lista com todos os autores cadastrados no sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de autores retornada com sucesso",
            content = @Content(schema = @Schema(implementation = Autor.class))
        )
    })
    @GetMapping
    public ResponseEntity<List<Autor>> listarTodos() {
        List<Autor> autores = autorRepository.findAll();
        return ResponseEntity.ok(autores);
    }

    @Operation(
        summary = "Buscar autor por ID",
        description = "Retorna um autor específico pelo seu ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Autor encontrado",
            content = @Content(schema = @Schema(implementation = Autor.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Autor não encontrado"
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Autor> buscarPorId(
            @Parameter(description = "ID do autor", required = true, example = "1")
            @PathVariable Long id) {
        return autorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Atualizar autor",
        description = "Atualiza as informações de um autor existente"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Autor atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = Autor.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Autor não encontrado"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos fornecidos"
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Autor> atualizar(
            @Parameter(description = "ID do autor", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Dados atualizados do autor", required = true)
            @Valid @RequestBody Autor autorAtualizado) {
        return autorRepository.findById(id)
                .map(autor -> {
                    autor.setNome(autorAtualizado.getNome());
                    autor.setNacionalidade(autorAtualizado.getNacionalidade());
                    autor.setDataNascimento(autorAtualizado.getDataNascimento());
                    autor.setBiografia(autorAtualizado.getBiografia());
                    return ResponseEntity.ok(autorRepository.save(autor));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Deletar autor",
        description = "Remove um autor do sistema pelo seu ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Autor deletado com sucesso"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Autor não encontrado"
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do autor", required = true, example = "1")
            @PathVariable Long id) {
        if (autorRepository.existsById(id)) {
            autorRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
