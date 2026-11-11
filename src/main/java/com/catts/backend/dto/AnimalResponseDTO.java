package com.catts.backend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AnimalResponseDTO {
    private String id;
    private Integer idOng;
    private String name;
    private Integer age;
    private String porte;
    private String localizacao;
    private Integer weight;
    private String description;
    private Boolean adoptionStatus;
    private Boolean castrado;
    private String vaccine;

    private OngResponseDTO ong;
    private PhotoResponseDTO image;   // dados da foto
}