package com.catts.backend.service;

import com.catts.backend.database.model.AnimalEntity;
import com.catts.backend.dto.EstiloImagem;
import com.catts.backend.dto.FotoEditadaResponseDTO;
import com.catts.backend.repository.AnimalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

// Fluxo: pega a foto original no Blob -> manda pra IA com o estilo escolhido -> salva em "editadas/"
@RequiredArgsConstructor
@Service
public class FotoEditadaService {

    private final AnimalRepository animalRepository;
    private final AzureBlobService azureBlobService;
    private final FoundryImageService foundryImageService;

    // Sem @Transactional de proposito: a chamada da IA demora e nao deve segurar o banco aberto
    public FotoEditadaResponseDTO editar(String animalId, EstiloImagem estilo) {

        AnimalEntity animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Animal não encontrado."));

        if (animal.getImage() == null || animal.getImage().getLinkId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Esse animal não tem foto cadastrada.");
        }

        String blobOriginal = animal.getImage().getLinkId();   // ex.: animais/uuid.jpg

        if (!azureBlobService.blobExiste(blobOriginal)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "A foto original não foi encontrada no armazenamento.");
        }

        byte[] original = azureBlobService.baixarBlob(blobOriginal);
        byte[] editada = foundryImageService.editar(original, estilo.getPrompt());

        // mesmo UUID da original, em outra "pasta": animais/abc.png -> editadas/abc.jpg
        String blobEditada = "editadas/" + nomeSemPastaEExtensao(blobOriginal) + ".jpg";
        azureBlobService.enviarBytes(blobEditada, editada, "image/jpeg");

        return new FotoEditadaResponseDTO(animalId, blobOriginal, blobEditada, estilo.name());
    }

    private String nomeSemPastaEExtensao(String blobName) {
        String nome = blobName.substring(blobName.lastIndexOf('/') + 1);
        int ponto = nome.lastIndexOf('.');
        return ponto > 0 ? nome.substring(0, ponto) : nome;
    }
}
