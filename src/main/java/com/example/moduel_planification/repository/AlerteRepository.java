package com.example.moduel_planification.repository;

import com.example.moduel_planification.entity.Alerte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AlerteRepository extends JpaRepository<Alerte, Integer> {

    @Query("SELECT COUNT(a) FROM Alerte a WHERE a.controleur.id = :controleurId AND a.dateCreation >= :since")
    long countByControleurSince(@Param("controleurId") String controleurId, @Param("since") LocalDateTime since);
}