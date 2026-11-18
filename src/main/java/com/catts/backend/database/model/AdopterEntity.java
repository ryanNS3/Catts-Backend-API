package com.catts.backend.database.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AdopterEntity {
    @Id
    private Long id;
    String CNPJ;
    String nomeInstitucional;
    String email;
    String senha;
    String telefone;
    String descricao;
}
