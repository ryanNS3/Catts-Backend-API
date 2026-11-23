package com.catts.backend.dto;

import com.catts.backend.database.model.PhotoAnimalEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PhotoResponseDTO {
    private String linkId;

    public static PhotoResponseDTO fromEntity(PhotoAnimalEntity entity) {
        if (entity == null) return null;
        return PhotoResponseDTO.builder()
                .linkId(entity.getLinkId())
                .build();
    }
}