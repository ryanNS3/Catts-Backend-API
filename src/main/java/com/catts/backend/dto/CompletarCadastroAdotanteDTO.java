package com.catts.backend.dto;

import com.catts.backend.enums.TipoResidenciaEnum;
import java.time.LocalDate;

public record CompletarCadastroAdotanteDTO(
        String googleToken,
        String contato,
        String cpf,
        LocalDate dataNascimento,
        TipoResidenciaEnum tipoResidencia,
        Boolean possuiTelaProtecao,
        Boolean imovelAlugado,
        Float salario,
        String quantidadePet,
        String quantidadeHorasTrabalho, // Alterado de Integer para String para bater com o Front
        Integer quantidadePessoasCasa,
        String descricao,
        Boolean aceitaFotos,
        AddressDTO address
) {
}