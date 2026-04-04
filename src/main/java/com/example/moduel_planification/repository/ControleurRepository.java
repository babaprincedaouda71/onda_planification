package com.example.moduel_planification.repository;

import com.example.moduel_planification.entity.Controleur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ControleurRepository extends JpaRepository<Controleur, String> {

    @Query("SELECT COUNT(c) FROM Controleur c WHERE c.equipe.idEquipe = :idEquipe AND c.id <> :controleurId")
    long countByEquipeExcluding(@Param("idEquipe") Integer idEquipe, @Param("controleurId") String controleurId);

    @Query("SELECT COUNT(c) FROM Controleur c WHERE c.equipe.idEquipe = :idEquipe")
    long countByEquipe(@Param("idEquipe") Integer idEquipe);
}