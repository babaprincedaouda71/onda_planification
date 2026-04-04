package com.example.moduel_planification.service;

import com.example.moduel_planification.entity.Controleur;
import com.example.moduel_planification.repository.ControleurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ControleurService {

    private final ControleurRepository controleurRepository;

    @Autowired
    public ControleurService(ControleurRepository controleurRepository) {
        this.controleurRepository = controleurRepository;
    }

    public List<Controleur> findAll() {
        return controleurRepository.findAll();
    }

    public Optional<Controleur> findById(String id) {
        return controleurRepository.findById(id);
    }

    public Controleur save(Controleur controleur) {
        return controleurRepository.save(controleur);
    }

    public void deleteById(String id) {
        controleurRepository.deleteById(id);
    }
}