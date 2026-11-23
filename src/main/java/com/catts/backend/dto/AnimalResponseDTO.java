package com.catts.backend.dto;

import com.catts.backend.database.model.AnimalEntity;
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
    private PhotoResponseDTO image;

    public static AnimalResponseDTO fromEntity(AnimalEntity entity) {
        if (entity == null) {
            return null;
        }

        return AnimalResponseDTO.builder()
                .id(entity.getId())
                .idOng(entity.getOng() != null ? entity.getOng().getId().intValue() : entity.getIdOng())
                .name(entity.getName())
                .age(entity.getAge())
                .porte(entity.getPorte())
                .localizacao(entity.getLocalizacao())
                .weight(entity.getWeight())
                .description(entity.getDescription())
                .adoptionStatus(entity.getAdoptionStatus())
                .castrado(entity.getCastrado())
                .vaccine(entity.getVaccine())
                .ong(entity.getOng() != null ? OngResponseDTO.fromEntity(entity.getOng()) : null)
                .image(entity.getImage() != null ? PhotoResponseDTO.fromEntity(entity.getImage()) : null)
                .build();
    }


}

