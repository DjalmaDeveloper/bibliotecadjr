package com.library.sistema_biblioteca.controller;

import com.library.sistema_biblioteca.model.Livro;
import com.library.sistema_biblioteca.repository.LivroRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/livros")
@CrossOrigin(origins = {"https://bibliotecadjr.pages.dev/", "http://localhost:3000"})
public class LivroController {

    @Autowired
    private LivroRepository livroRepository;

    @PostMapping
    public ResponseEntity<Livro> criar(@Valid @RequestBody Livro livro) {
        Livro novoLivro = livroRepository.save(livro);
        return ResponseEntity.ok(novoLivro);
    }

    @GetMapping
    public ResponseEntity<List<Livro>> listarTodos() {
        List<Livro> livros = livroRepository.findAll();
        return ResponseEntity.ok(livros);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Livro> buscarPorId(@PathVariable Long id) {
        return livroRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Livro> atualizar(@PathVariable Long id,
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (livroRepository.existsById(id)) {
            livroRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
