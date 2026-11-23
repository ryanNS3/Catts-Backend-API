package com.catts.backend.dto;

import com.catts.backend.database.model.AdoptionRequestEntity;

import java.time.LocalDateTime;

public record SolicitacaoAdocaoResponseDTO(
        Long id,
        Long adopterId,
        String nomeAdotante,
        Long ongId,
        String nomeOng,
        String animalId,
        String nomeAnimal,
        String status,
        String mensagemAdotante,
        String respostaOng,
        LocalDateTime dataSolicitacao
) {

    public static SolicitacaoAdocaoResponseDTO fromEntity(AdoptionRequestEntity entity) {
        return new SolicitacaoAdocaoResponseDTO(
                entity.getId(),
                entity.getAdopter() != null ? entity.getAdopter().getId() : null,
                entity.getAdopter() != null ? entity.getAdopter().getNome() : null,
                entity.getOng() != null ? entity.getOng().getId() : null,
                entity.getOng() != null ? entity.getOng().getNomeInstitucional() : null,
                entity.getAnimal() != null ? entity.getAnimal().getId() : null,
                entity.getAnimal() != null ? entity.getAnimal().getName() : null,
                entity.getStatus() != null ? entity.getStatus().name() : null,
                entity.getMensagemAdotante(),
                entity.getRespostaOng(),
                entity.getDataSolicitacao()
        );
    }
}
