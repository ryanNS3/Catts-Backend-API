package com.catts.backend.controller;

import com.catts.backend.database.model.AdopterEntity;
import com.catts.backend.database.model.OngEntity;
import com.catts.backend.dto.CriarSolicitacaoAdocaoDTO;
import com.catts.backend.dto.ResponderSolicitacaoAdocaoDTO;
import com.catts.backend.dto.SolicitacaoAdocaoResponseDTO;
import com.catts.backend.service.AdoptionRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/adocoes")
public class AdoptionRequestController {

    private final AdoptionRequestService adoptionRequestService;

    public AdoptionRequestController(AdoptionRequestService adoptionRequestService) {
        this.adoptionRequestService = adoptionRequestService;
    }

    // 1. Adotante cria requisição de adoção
    @PostMapping
    public ResponseEntity<SolicitacaoAdocaoResponseDTO> criarSolicitacao(
            @AuthenticationPrincipal Object principal,
            @RequestBody CriarSolicitacaoAdocaoDTO dto) {

        if (!(principal instanceof AdopterEntity adotante)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas adotantes podem solicitar adoção.");
        }

        SolicitacaoAdocaoResponseDTO response = adoptionRequestService.criarSolicitacao(
                adotante.getId(), "ADOTANTE", dto
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. ONG aprova ou reprova a requisição
    @PatchMapping("/{id}/responder")
    public ResponseEntity<SolicitacaoAdocaoResponseDTO> responderSolicitacao(
            @PathVariable Long id,
            @AuthenticationPrincipal Object principal,
            @RequestBody ResponderSolicitacaoAdocaoDTO dto) {

        if (!(principal instanceof OngEntity ong)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas ONGs podem aprovar ou reprovar solicitações.");
        }

        SolicitacaoAdocaoResponseDTO response = adoptionRequestService.responderSolicitacao(
                id, ong.getId(), "ONG", dto
        );
        return ResponseEntity.ok(response);
    }

    // Rotas auxiliares para consultar solicitações
    @GetMapping("/minhas")
    public ResponseEntity<List<SolicitacaoAdocaoResponseDTO>> minhasSolicitacoes(
            @AuthenticationPrincipal Object principal) {

        if (!(principal instanceof AdopterEntity adotante)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso permitido apenas para adotantes.");
        }

        return ResponseEntity.ok(adoptionRequestService.listarPorAdotante(adotante.getId()));
    }

    @GetMapping("/ong")
    public ResponseEntity<List<SolicitacaoAdocaoResponseDTO>> solicitacoesDaOng(
            @AuthenticationPrincipal Object principal) {

        if (!(principal instanceof OngEntity ong)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso permitido apenas para ONGs.");
        }

        return ResponseEntity.ok(adoptionRequestService.listarPorOng(ong.getId()));
    }
}