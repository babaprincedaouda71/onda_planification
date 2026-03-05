package com.example.moduel_planification.web;

import com.example.moduel_planification.entity.Controleur;
import com.example.moduel_planification.service.ControleurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/controleurs")
public class ControleurController {

    private final ControleurService controleurService;

    @Autowired
    public ControleurController(ControleurService controleurService) {
        this.controleurService = controleurService;
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
    public Controleur createControleur(@RequestBody Controleur controleur) {
        return controleurService.save(controleur);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Controleur> updateControleur(@PathVariable String id, @RequestBody Controleur controleur) {
        return controleurService.findById(id)
                .map(existingControleur -> {
                    controleur.setId(id);
                    return ResponseEntity.ok(controleurService.save(controleur));
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