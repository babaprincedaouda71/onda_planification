package com.example.moduel_planification.web;

import com.example.moduel_planification.entity.Alerte;
import com.example.moduel_planification.service.AlerteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alertes")
public class AlerteController {

    private final AlerteService alerteService;

    @Autowired
    public AlerteController(AlerteService alerteService) {
        this.alerteService = alerteService;
    }

    @GetMapping
    public List<Alerte> getAllAlertes() {
        return alerteService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alerte> getAlerteById(@PathVariable Integer id) {
        return alerteService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Alerte createAlerte(@RequestBody Alerte alerte) {
        return alerteService.save(alerte);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Alerte> updateAlerte(@PathVariable Integer id, @RequestBody Alerte alerte) {
        return alerteService.findById(id)
                .map(existingAlerte -> {
                    alerte.setIdAlerte(id);
                    return ResponseEntity.ok(alerteService.save(alerte));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlerte(@PathVariable Integer id) {
        if (alerteService.findById(id).isPresent()) {
            alerteService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}