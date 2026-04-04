package com.example.moduel_planification.web;

import com.example.moduel_planification.entity.Affectation;
import com.example.moduel_planification.exception.ViolationReglementaireException;
import com.example.moduel_planification.service.AffectationService;
import com.example.moduel_planification.service.ReglementaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/affectations")
public class AffectationController {

    private final AffectationService affectationService;
    private final ReglementaireService reglementaireService;

    @Autowired
    public AffectationController(AffectationService affectationService, ReglementaireService reglementaireService) {
        this.affectationService = affectationService;
        this.reglementaireService = reglementaireService;
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
    public ResponseEntity<?> createAffectation(@RequestBody Affectation affectation) {
        try {
            reglementaireService.validerAffectation(affectation);
            return ResponseEntity.ok(affectationService.save(affectation));
        } catch (ViolationReglementaireException e) {
            return ResponseEntity.badRequest().body(Map.of("code", e.getCode(), "message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAffectation(@PathVariable Integer id, @RequestBody Affectation affectation) {
        return affectationService.findById(id)
                .map(existingAffectation -> {
                    try {
                        reglementaireService.validerAffectation(affectation);
                        affectation.setIdAffectation(id);
                        return ResponseEntity.ok((Object) affectationService.save(affectation));
                    } catch (ViolationReglementaireException e) {
                        return ResponseEntity.badRequest().body((Object) Map.of("code", e.getCode(), "message", e.getMessage()));
                    }
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