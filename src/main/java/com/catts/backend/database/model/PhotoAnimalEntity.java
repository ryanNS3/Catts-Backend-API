package com.catts.backend.database.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "photo_animal_entity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "animal")
public class PhotoAnimalEntity {

    @Id
    @Column(name = "link_id", unique = true)
    private String linkId;

    @OneToOne
    @JoinColumn(name = "animal_id", referencedColumnName = "id")
    @JsonBackReference
    private AnimalEntity animal;
}