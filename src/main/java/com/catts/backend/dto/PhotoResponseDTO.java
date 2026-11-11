package com.catts.backend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PhotoResponseDTO {
    private String linkId;   // nome do arquivo pra colocar no Blob tipo: "animais/auhsuh23.jpg"
}