package com.example.moduel_planification.web;

import com.example.moduel_planification.entity.Controleur;
import com.example.moduel_planification.exception.ViolationReglementaireException;
import com.example.moduel_planification.service.ControleurService;
import com.example.moduel_planification.service.ReglementaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/controleurs")
public class ControleurController {

    private final ControleurService controleurService;
    private final ReglementaireService reglementaireService;

    @Autowired
    public ControleurController(ControleurService controleurService, ReglementaireService reglementaireService) {
        this.controleurService = controleurService;
        this.reglementaireService = reglementaireService;
    }

    @GetMapping
    public List<Controleur> getAllControleurs() {
        return controleurService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Controleur> getControleurById(@PathVariable String id) {
        return controleurService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createControleur(@RequestBody Controleur controleur) {
        try {
            reglementaireService.validerTailleEquipe(controleur, false);
            return ResponseEntity.ok(controleurService.save(controleur));
        } catch (ViolationReglementaireException e) {
            return ResponseEntity.badRequest().body(Map.of("code", e.getCode(), "message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateControleur(@PathVariable String id, @RequestBody Controleur controleur) {
        return controleurService.findById(id)
                .map(existingControleur -> {
                    try {
                        controleur.setId(id);
                        // Valider RM-P1 seulement si l'équipe change
                        boolean equipeChange = existingControleur.getEquipe() == null
                                || controleur.getEquipe() == null
                                || !existingControleur.getEquipe().getIdEquipe().equals(
                                        controleur.getEquipe().getIdEquipe());
                        if (equipeChange) {
                            reglementaireService.validerTailleEquipe(controleur, true);
                        }
                        return ResponseEntity.ok((Object) controleurService.save(controleur));
                    } catch (ViolationReglementaireException e) {
                        return ResponseEntity.badRequest().body(
                                (Object) Map.of("code", e.getCode(), "message", e.getMessage()));
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteControleur(@PathVariable String id) {
        if (controleurService.findById(id).isPresent()) {
            controleurService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}