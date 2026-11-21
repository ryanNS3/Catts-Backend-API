package com.catts.backend.dto;

// Resposta da edicao: nomes dos arquivos no Blob (container "imagens")
public record FotoEditadaResponseDTO(
        String animalId,
        String fotoOriginal,   // ex.: animais/uuid.jpg
        String fotoEditada,    // ex.: editadas/uuid.jpg
        String estilo          // ex.: FUNDO_COLORIDO
) {}
