package com.example.moduel_planification.repository;

import com.example.moduel_planification.entity.Controleur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ControleurRepository extends JpaRepository<Controleur, String> {
}