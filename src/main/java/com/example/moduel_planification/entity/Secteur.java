package com.example.moduel_planification.entity;

import com.example.moduel_planification.entity.enums.ComplexiteSecteur;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Secteur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSecteur;

    @Enumerated(EnumType.STRING)
    private ComplexiteSecteur complexite;

    @ManyToOne
    @JoinColumn(name = "id_shift")
    private Shift shift;

    @OneToMany(mappedBy = "secteur")
    private List<Affectation> affectations;
}