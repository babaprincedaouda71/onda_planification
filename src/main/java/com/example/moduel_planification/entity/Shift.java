package com.example.moduel_planification.entity;

import com.example.moduel_planification.enums.TypeShift;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idShift;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private TypeShift typeShift;

    @ManyToOne
    @JoinColumn(name = "id_equipe")
    private Equipe equipe;

    @OneToMany(mappedBy = "shift")
    @JsonIgnore
    private List<Secteur> secteurs;
}