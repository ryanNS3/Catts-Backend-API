package com.catts.backend.dto;

public record AuthResponseDTO(
        boolean precisaCompletarCadastro,
        String token,
        String tipo,
        String email,
        String nome

) {
}
