package com.catts.backend.controller;

import com.catts.backend.service.AzureBlobService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/imagens")
public class AzureBlobController {

    private final AzureBlobService azureBlobService;

    public AzureBlobController(AzureBlobService azureBlobService) {
        this.azureBlobService = azureBlobService;
    }

    @PostMapping(
            value = "/animal",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Map<String, String>> uploadAnimal(
            @RequestParam("arquivo") MultipartFile arquivo
    ) throws IOException {

        String nomeArquivo =
                azureBlobService.uploadImagemAnimal(arquivo);

        Map<String, String> resposta = Map.of(
                "mensagem", "Imagem enviada com sucesso!",
                "nomeArquivo", nomeArquivo,
                "container", "imagens"
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resposta);
    }
}