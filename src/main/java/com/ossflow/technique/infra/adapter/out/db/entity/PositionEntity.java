package com.ossflow.technique.infra.adapter.out.db.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "positions")
@Data
@NoArgsConstructor
public class PositionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String type; // Guardaremos el Enum como String en base de datos

    @OneToMany(mappedBy = "startPosition", cascade = CascadeType.ALL)
    private List<TechniqueEntity> availableTechniques;
}