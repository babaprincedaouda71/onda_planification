package com.example.moduel_planification.service;

import com.example.moduel_planification.entity.Shift;
import com.example.moduel_planification.repository.ShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShiftService {

    private final ShiftRepository shiftRepository;

    @Autowired
    public ShiftService(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    public List<Shift> findAll() {
        return shiftRepository.findAll();
    }

    public Optional<Shift> findById(Integer id) {
        return shiftRepository.findById(id);
    }

    public Shift save(Shift shift) {
        return shiftRepository.save(shift);
    }

    public void deleteById(Integer id) {
        shiftRepository.deleteById(id);
    }
}