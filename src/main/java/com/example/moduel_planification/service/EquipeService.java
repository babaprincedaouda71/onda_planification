package com.example.moduel_planification.service;

import com.example.moduel_planification.entity.Equipe;
import com.example.moduel_planification.repository.EquipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EquipeService {

    private final EquipeRepository equipeRepository;

    @Autowired
    public EquipeService(EquipeRepository equipeRepository) {
        this.equipeRepository = equipeRepository;
    }

    public List<Equipe> findAll() {
        return equipeRepository.findAll();
    }

    public Optional<Equipe> findById(Integer id) {
        return equipeRepository.findById(id);
    }

    public Equipe save(Equipe equipe) {
        return equipeRepository.save(equipe);
    }

    public void deleteById(Integer id) {
        equipeRepository.deleteById(id);
    }
}