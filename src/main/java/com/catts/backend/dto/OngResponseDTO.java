package com.catts.backend.dto;

import com.catts.backend.database.model.OngEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OngResponseDTO {
    private Long id;
    private String cnpj;
    private String nomeInstitucional;
    private String email;
    private String contato;
    private String instagram;
    private String facebook;
    private String horarioInicio;
    private String horarioFim;
    private String diaDisponibilidade;

    public static OngResponseDTO fromEntity(OngEntity entity) {
        if (entity == null) return null;
        return OngResponseDTO.builder()
                .id(entity.getId())
                .cnpj(entity.getCnpj())
                .nomeInstitucional(entity.getNomeInstitucional())
                .email(entity.getEmail())
                .contato(entity.getContato())
                .instagram(entity.getInstagram())
                .facebook(entity.getFacebook())
                .horarioInicio(entity.getHorarioInicio())
                .horarioFim(entity.getHorarioFim())
                .diaDisponibilidade(entity.getDiaDisponibilidade())
                .build();
    }
}