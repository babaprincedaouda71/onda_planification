package com.example.moduel_planification.service;

import com.example.moduel_planification.entity.Affectation;
import com.example.moduel_planification.repository.AffectationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AffectationService {

    private final AffectationRepository affectationRepository;

    @Autowired
    public AffectationService(AffectationRepository affectationRepository) {
        this.affectationRepository = affectationRepository;
    }

    public List<Affectation> findAll() {
        return affectationRepository.findAll();
    }

    public Optional<Affectation> findById(Integer id) {
        return affectationRepository.findById(id);
    }

    public Affectation save(Affectation affectation) {
        return affectationRepository.save(affectation);
    }

    public void deleteById(Integer id) {
        affectationRepository.deleteById(id);
    }
}