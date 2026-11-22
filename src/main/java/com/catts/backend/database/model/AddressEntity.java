package com.catts.backend.database.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "address_entity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String street;
    private Integer number;
    private String city;
    private String state;
    private String cep;
}