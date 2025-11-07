package com.library.sistema_biblioteca.controller;

import com.library.sistema_biblioteca.model.Autor;
import com.library.sistema_biblioteca.repository.AutorRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/autores")
@CrossOrigin(origins = {"https://bibliotecadjr.pages.dev/", "http://localhost:3000"}) // Permite acesso do frontend
public class AutorController {

    @Autowired
    private AutorRepository autorRepository;

    // CREATE - Criar novo autor
    @PostMapping
    public ResponseEntity<Autor> criar(@Valid @RequestBody Autor autor) {
        Autor novoAutor = autorRepository.save(autor);
        return ResponseEntity.ok(novoAutor);
    }

    // READ - Listar todos os autores
    @GetMapping
    public ResponseEntity<List<Autor>> listarTodos() {
        List<Autor> autores = autorRepository.findAll();
        return ResponseEntity.ok(autores);
    }

    // READ - Buscar autor por ID
    @GetMapping("/{id}")
    public ResponseEntity<Autor> buscarPorId(@PathVariable Long id) {
        return autorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE - Atualizar autor
    @PutMapping("/{id}")
    public ResponseEntity<Autor> atualizar(@PathVariable Long id, @Valid @RequestBody Autor autorAtualizado) {
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

    // DELETE - Deletar autor
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (autorRepository.existsById(id)) {
            autorRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
