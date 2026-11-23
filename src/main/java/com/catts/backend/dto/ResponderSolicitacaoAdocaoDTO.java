package com.catts.backend.dto;

public record ResponderSolicitacaoAdocaoDTO(
        boolean aprovar, // true = APROVADA, false = REJEITADA
        String respostaOng
) {
}
