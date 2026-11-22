package com.catts.backend.database.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Animal")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"ong", "image"})
public class AnimalEntity {

    @Id
    private String id;

    @Column(name = "idOng")
    private Integer idOng;

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private Integer age;

    @Column(name = "weight")
    private Integer weight;

    @Column(name = "description")
    private String description;

    @Column(name = "porte")
    private String porte;

    @Column(name = "localizacao")
    private String localizacao;

    @Column(name = "adoptionStatus")
    private Boolean adoptionStatus;

    @Column(name = "castrado")
    private Boolean castrado;

    @Column(name = "vaccine")
    private String vaccine;

    @ManyToOne
    @JoinColumn(name = "CNPJ_id", referencedColumnName = "CNPJ")
    @JsonBackReference
    private OngEntity ong;

    @OneToOne(mappedBy = "animal")
    @JsonManagedReference
    private PhotoAnimalEntity image;
}