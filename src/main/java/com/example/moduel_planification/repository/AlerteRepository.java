package com.example.moduel_planification.repository;

import com.example.moduel_planification.entity.Alerte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlerteRepository extends JpaRepository<Alerte, Integer> {
}