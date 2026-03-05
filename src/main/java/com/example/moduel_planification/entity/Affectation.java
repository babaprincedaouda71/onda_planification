package com.example.moduel_planification.entity;

import com.example.moduel_planification.enums.RoleAffectation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Affectation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAffectation;

    @Enumerated(EnumType.STRING)
    private RoleAffectation role;

    @ManyToOne
    @JoinColumn(name = "id_controleur")
    private Controleur controleur;

    @ManyToOne
    @JoinColumn(name = "id_secteur")
    private Secteur secteur;
}