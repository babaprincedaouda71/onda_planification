package com.example.moduel_planification.web;

import com.example.moduel_planification.entity.Affectation;
import com.example.moduel_planification.service.AffectationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/affectations")
public class AffectationController {

    private final AffectationService affectationService;

    @Autowired
    public AffectationController(AffectationService affectationService) {
        this.affectationService = affectationService;
    }

    @GetMapping
    public List<Affectation> getAllAffectations() {
        return affectationService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Affectation> getAffectationById(@PathVariable Integer id) {
        return affectationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Affectation createAffectation(@RequestBody Affectation affectation) {
        return affectationService.save(affectation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Affectation> updateAffectation(@PathVariable Integer id, @RequestBody Affectation affectation) {
        return affectationService.findById(id)
                .map(existingAffectation -> {
                    affectation.setIdAffectation(id);
                    return ResponseEntity.ok(affectationService.save(affectation));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAffectation(@PathVariable Integer id) {
        if (affectationService.findById(id).isPresent()) {
            affectationService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}