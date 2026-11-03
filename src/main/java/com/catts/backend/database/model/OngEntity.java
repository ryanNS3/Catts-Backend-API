package com.catts.backend.database.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
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

    // Na OngEntity, em cima da lista de animais:
    @JsonIgnore
    @OneToMany(mappedBy = "ong")
    private List<AnimalEntity> animal;


}
