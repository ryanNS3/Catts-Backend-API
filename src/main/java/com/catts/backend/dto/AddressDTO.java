package com.catts.backend.dto;

public record AddressDTO(
        String street,
        Integer number,
        String city,
        String state,
        String cep
) {
}
