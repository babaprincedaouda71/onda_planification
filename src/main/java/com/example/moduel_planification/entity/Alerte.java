package com.example.moduel_planification.entity;

import com.example.moduel_planification.enums.NiveauAlerte;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alerte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAlerte;

    private String typeAlerte;

    @Enumerated(EnumType.STRING)
    private NiveauAlerte niveau;

    @ManyToOne
    @JoinColumn(name = "id_controleur")
    private Controleur controleur;

    @ManyToOne
    @JoinColumn(name = "id_equipe")
    private Equipe equipe;
}