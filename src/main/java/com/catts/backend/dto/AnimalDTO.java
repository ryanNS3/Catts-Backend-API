package com.catts.backend.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AnimalDTO {
    private String cnpjOng;       // era Integer idOng
    private String nome;
    private Integer idade;
    private String porte;
    private String descricao;
    private Boolean statusAdocao;
    private String localizacao;
}