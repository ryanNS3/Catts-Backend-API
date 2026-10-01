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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private Integer idOng;
    private String name;
    private Integer age;
    private String porte;
    private String description;
    private Boolean AdoptionStatus;

    @ManyToOne
    @JoinColumn(name = "CNPJ_id")
    private OngEntity ong;

    @OneToOne(mappedBy = "animal")
    private PhotoAnimalEntity image;



}
