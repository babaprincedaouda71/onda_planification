package com.example.moduel_planification.repository;

import com.example.moduel_planification.entity.Secteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecteurRepository extends JpaRepository<Secteur, Integer> {
}