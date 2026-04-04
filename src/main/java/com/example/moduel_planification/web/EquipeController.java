package com.example.moduel_planification.web;

import com.example.moduel_planification.entity.Equipe;
import com.example.moduel_planification.service.EquipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipes")
public class EquipeController {

    private final EquipeService equipeService;

    @Autowired
    public EquipeController(EquipeService equipeService) {
        this.equipeService = equipeService;
    }

    @GetMapping
    public List<Equipe> getAllEquipes() {
        return equipeService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Equipe> getEquipeById(@PathVariable Integer id) {
        return equipeService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Equipe createEquipe(@RequestBody Equipe equipe) {
        return equipeService.save(equipe);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Equipe> updateEquipe(@PathVariable Integer id, @RequestBody Equipe equipe) {
        return equipeService.findById(id)
                .map(existingEquipe -> {
                    equipe.setIdEquipe(id);
                    return ResponseEntity.ok(equipeService.save(equipe));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEquipe(@PathVariable Integer id) {
        if (equipeService.findById(id).isPresent()) {
            equipeService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}