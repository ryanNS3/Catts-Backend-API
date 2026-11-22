package com.catts.backend.dto;

import com.catts.backend.enums.TipoResidenciaEnum;

import java.time.LocalDate;

public record CompletarCadastroAdotanteDTO(
        String googleToken,
        String contato,
        String cpf,
        LocalDate dataNascimento,
        TipoResidenciaEnum tipoResidencia, // O Spring aceita "CONDOMINIO" ou "INDEPENDENTE" direto do JSON
        Boolean possuiTelaProtecao,
        Boolean imovelAlugado,
        Float salario,
        String quantidadePet,
        Integer quantidadeHorasTrabalho,
        Integer quantidadePessoasCasa,
        String descricao,
        AddressDTO address // Objeto aninhado com os dados do endereço
) {
}
