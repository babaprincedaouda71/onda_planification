package com.example.moduel_planification.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Equipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEquipe;

    private String nomEquipe;

    private Integer maxMembres;

    @OneToMany(mappedBy = "equipe")
    @JsonIgnore
    private List<Controleur> membresEquipe;

    @OneToMany(mappedBy = "equipe")
    @JsonIgnore
    private List<Shift> shifts;

    @OneToMany(mappedBy = "equipe")
    @JsonIgnore
    private List<Alerte> alertes;
}