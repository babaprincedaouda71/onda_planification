package com.example.moduel_planification.web;

import com.example.moduel_planification.entity.Shift;
import com.example.moduel_planification.exception.ViolationReglementaireException;
import com.example.moduel_planification.service.ReglementaireService;
import com.example.moduel_planification.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shifts")
public class ShiftController {

    private final ShiftService shiftService;
    private final ReglementaireService reglementaireService;

    @Autowired
    public ShiftController(ShiftService shiftService, ReglementaireService reglementaireService) {
        this.shiftService = shiftService;
        this.reglementaireService = reglementaireService;
    }

    @GetMapping
    public List<Shift> getAllShifts() {
        return shiftService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Shift> getShiftById(@PathVariable Integer id) {
        return shiftService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createShift(@RequestBody Shift shift) {
        try {
            reglementaireService.validerDureeShift(shift);
            return ResponseEntity.ok(shiftService.save(shift));
        } catch (ViolationReglementaireException e) {
            return ResponseEntity.badRequest().body(Map.of("code", e.getCode(), "message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateShift(@PathVariable Integer id, @RequestBody Shift shift) {
        return shiftService.findById(id)
                .map(existingShift -> {
                    try {
                        reglementaireService.validerDureeShift(shift);
                        shift.setIdShift(id);
                        return ResponseEntity.ok((Object) shiftService.save(shift));
                    } catch (ViolationReglementaireException e) {
                        return ResponseEntity.badRequest().body((Object) Map.of("code", e.getCode(), "message", e.getMessage()));
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShift(@PathVariable Integer id) {
        if (shiftService.findById(id).isPresent()) {
            shiftService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}