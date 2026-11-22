package com.catts.backend.dto;

public record CompletarCadastroOngDTO(
        String googleToken,
        String CNPJ,
        String nomeInstitucional,
        String email,
        String contato,
        String instagram,
        String facebook,
        String horarioInicio,
        String horarioFim,
        String diaDisponibilidade,
        AddressDTO address
) {
}
