package com.example.moduel_planification.entity;

import com.example.moduel_planification.entity.enums.ProfilType;
import com.example.moduel_planification.entity.enums.SurveillanceLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Controleur {

    @Id
    private String id;

    private String name;

    private String sexe;

    private Integer age;

    private Integer anciennete;

    private Integer sensibiliteStress;

    private String tache;

    @Enumerated(EnumType.STRING)
    private ProfilType profil;

    @Enumerated(EnumType.STRING)
    private SurveillanceLevel surveillance;

    private String shiftStart;

    private String shiftEnd;

    private String status;

    @OneToMany(mappedBy = "controleur")
    private List<Alerte> alertes;

    @ManyToOne
    @JoinColumn(name = "id_equipe")
    private Equipe equipe;

    @OneToMany(mappedBy = "controleur")
    private List<Affectation> affectations;
}