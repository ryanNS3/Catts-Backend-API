package com.catts.backend.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.catts.backend.dto.CriarSolicitacaoAdocaoDTO;
import com.catts.backend.dto.ResponderSolicitacaoAdocaoDTO;
import com.catts.backend.dto.SolicitacaoAdocaoResponseDTO;
import com.catts.backend.service.AdoptionRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
            @AuthenticationPrincipal DecodedJWT jwt,
            @RequestBody CriarSolicitacaoAdocaoDTO dto) {

        Long adopterId = jwt.getClaim("id").asLong();
        String tipoUsuario = jwt.getClaim("tipo").asString();

        SolicitacaoAdocaoResponseDTO response = adoptionRequestService.criarSolicitacao(adopterId, tipoUsuario, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. ONG aprova ou reprova a requisição
    @PatchMapping("/{id}/responder")
    public ResponseEntity<SolicitacaoAdocaoResponseDTO> responderSolicitacao(
            @PathVariable Long id,
            @AuthenticationPrincipal DecodedJWT jwt,
            @RequestBody ResponderSolicitacaoAdocaoDTO dto) {

        Long ongId = jwt.getClaim("id").asLong();
        String tipoUsuario = jwt.getClaim("tipo").asString();

        SolicitacaoAdocaoResponseDTO response = adoptionRequestService.responderSolicitacao(id, ongId, tipoUsuario, dto);
        return ResponseEntity.ok(response);
    }

    // Rotas auxiliares para consultar solicitações
    @GetMapping("/minhas")
    public ResponseEntity<List<SolicitacaoAdocaoResponseDTO>> minhasSolicitacoes(
            @AuthenticationPrincipal DecodedJWT jwt) {

        Long adopterId = jwt.getClaim("id").asLong();
        return ResponseEntity.ok(adoptionRequestService.listarPorAdotante(adopterId));
    }

    @GetMapping("/ong")
    public ResponseEntity<List<SolicitacaoAdocaoResponseDTO>> solicitacoesDaOng(
            @AuthenticationPrincipal DecodedJWT jwt) {

        Long ongId = jwt.getClaim("id").asLong();
        return ResponseEntity.ok(adoptionRequestService.listarPorOng(ongId));
    }
}