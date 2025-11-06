package com.library.sistema_biblioteca.controller;

import com.library.sistema_biblioteca.model.Emprestimo;
import com.library.sistema_biblioteca.repository.EmprestimoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emprestimos")
@CrossOrigin(origins = {"https://bibliotecadjr.pages.dev/", "http://localhost:3000"})
public class EmprestimoController {

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @PostMapping
    public ResponseEntity<Emprestimo> criar(@Valid @RequestBody Emprestimo emprestimo) {
        Emprestimo novoEmprestimo = emprestimoRepository.save(emprestimo);
        return ResponseEntity.ok(novoEmprestimo);
    }

    @GetMapping
    public ResponseEntity<List<Emprestimo>> listarTodos() {
        List<Emprestimo> emprestimos = emprestimoRepository.findAll();
        return ResponseEntity.ok(emprestimos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Emprestimo> buscarPorId(@PathVariable Long id) {
        return emprestimoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Emprestimo> atualizar(@PathVariable Long id,
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (emprestimoRepository.existsById(id)) {
            emprestimoRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
