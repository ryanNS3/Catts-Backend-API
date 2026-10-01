package com.catts.backend.database.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PhotoAnimalEntity {
    @Id
    @Column(unique = true)
    String linkId;

    @OneToOne
    @JoinColumn(name = "animal_id")
    private AnimalEntity animal;

}
