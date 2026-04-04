package com.example.moduel_planification.service;

import com.example.moduel_planification.entity.Secteur;
import com.example.moduel_planification.repository.SecteurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SecteurService {

    private final SecteurRepository secteurRepository;

    @Autowired
    public SecteurService(SecteurRepository secteurRepository) {
        this.secteurRepository = secteurRepository;
    }

    public List<Secteur> findAll() {
        return secteurRepository.findAll();
    }

    public Optional<Secteur> findById(Integer id) {
        return secteurRepository.findById(id);
    }

    public Secteur save(Secteur secteur) {
        return secteurRepository.save(secteur);
    }

    public void deleteById(Integer id) {
        secteurRepository.deleteById(id);
    }
}