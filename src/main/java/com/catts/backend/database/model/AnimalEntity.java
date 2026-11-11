package com.catts.backend.database.model;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "Animal")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AnimalEntity {
    @Id
    private String id;
    private String name;
    private Integer age;
    private String porte;
    private String localizacao;
    private Integer weight;
    private String description;
    private Boolean adoptionStatus;
    private Boolean castrado;
    private String vaccine;

    @ManyToOne
    @JoinColumn(name = "CNPJ_id")
    @ToString.Exclude
    private OngEntity ong;

    @OneToOne(mappedBy = "animal")
    @ToString.Exclude
    private PhotoAnimalEntity image;
}