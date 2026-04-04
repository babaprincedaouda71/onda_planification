package com.example.moduel_planification.service;

import com.example.moduel_planification.entity.Alerte;
import com.example.moduel_planification.repository.AlerteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlerteService {

    private final AlerteRepository alerteRepository;

    @Autowired
    public AlerteService(AlerteRepository alerteRepository) {
        this.alerteRepository = alerteRepository;
    }

    public List<Alerte> findAll() {
        return alerteRepository.findAll();
    }

    public Optional<Alerte> findById(Integer id) {
        return alerteRepository.findById(id);
    }

    public Alerte save(Alerte alerte) {
        return alerteRepository.save(alerte);
    }

    public void deleteById(Integer id) {
        alerteRepository.deleteById(id);
    }
}