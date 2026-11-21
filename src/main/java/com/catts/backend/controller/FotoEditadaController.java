package com.catts.backend.controller;

import com.catts.backend.dto.EstiloImagem;
import com.catts.backend.dto.FotoEditadaResponseDTO;
import com.catts.backend.service.FotoEditadaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/animal")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class FotoEditadaController {

    private final FotoEditadaService fotoEditadaService;

    // Edita a foto do animal com IA. Ex.: POST /v1/animal/{id}/foto-editada?estilo=FUNDO_COLORIDO
    @PostMapping("/{id}/foto-editada")
    @ResponseStatus(HttpStatus.OK)
    public FotoEditadaResponseDTO editarFoto(
            @PathVariable("id") String id,
            @RequestParam("estilo") EstiloImagem estilo) {
        return fotoEditadaService.editar(id, estilo);
    }
}