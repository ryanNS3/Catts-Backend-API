package com.catts.backend.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OngResponseDTO {
    private String cnpj;
    private String nomeInstitucional;
    private String email;
    private String telefone;
    private String descricao;
}