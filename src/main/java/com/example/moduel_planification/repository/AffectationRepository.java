package com.example.moduel_planification.repository;

import com.example.moduel_planification.entity.Affectation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AffectationRepository extends JpaRepository<Affectation, Integer> {

    @Query("SELECT a FROM Affectation a JOIN FETCH a.secteur s LEFT JOIN FETCH s.shift sh WHERE a.controleur.id = :controleurId")
    List<Affectation> findByControleurIdWithShift(@Param("controleurId") String controleurId);

    @Query("SELECT a FROM Affectation a WHERE a.secteur.idSecteur = :idSecteur")
    List<Affectation> findBySecteur(@Param("idSecteur") Integer idSecteur);
}