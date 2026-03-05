package com.example.moduel_planification.web;

import com.example.moduel_planification.entity.Secteur;
import com.example.moduel_planification.service.SecteurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/secteurs")
public class SecteurController {

    private final SecteurService secteurService;

    @Autowired
    public SecteurController(SecteurService secteurService) {
        this.secteurService = secteurService;
    }

    @GetMapping
    public List<Secteur> getAllSecteurs() {
        return secteurService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Secteur> getSecteurById(@PathVariable Integer id) {
        return secteurService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Secteur createSecteur(@RequestBody Secteur secteur) {
        return secteurService.save(secteur);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Secteur> updateSecteur(@PathVariable Integer id, @RequestBody Secteur secteur) {
        return secteurService.findById(id)
                .map(existingSecteur -> {
                    secteur.setIdSecteur(id);
                    return ResponseEntity.ok(secteurService.save(secteur));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSecteur(@PathVariable Integer id) {
        if (secteurService.findById(id).isPresent()) {
            secteurService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}