package com.catts.backend.database.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OngEntity {
    @Id
    @Column(unique = true)
    String CNPJ;
    String nomeInstitucional;
    String email;
    String senha;
    String telefone;
    String descricao;

    @OneToOne
    @JoinColumn(name = "address")
    private AddressEntity address;

    @OneToMany(mappedBy = "ong")
    private Set<AnimalEntity> animal;


}
