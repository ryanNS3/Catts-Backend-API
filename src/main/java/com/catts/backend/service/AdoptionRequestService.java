package com.catts.backend.service;

import com.catts.backend.database.model.AdopterEntity;
import com.catts.backend.database.model.AdoptionRequestEntity;
import com.catts.backend.database.model.AnimalEntity;
import com.catts.backend.database.model.OngEntity;
import com.catts.backend.dto.CriarSolicitacaoAdocaoDTO;
import com.catts.backend.dto.ResponderSolicitacaoAdocaoDTO;
import com.catts.backend.dto.SolicitacaoAdocaoResponseDTO;
import com.catts.backend.enums.StatusAdocaoEnum;
import com.catts.backend.repository.AdopterRepository;
import com.catts.backend.repository.AdoptionRequestRepository;
import com.catts.backend.repository.AnimalRepository;
import com.catts.backend.repository.OngRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdoptionRequestService {

    private final AdoptionRequestRepository adoptionRequestRepository;
    private final AdopterRepository adopterRepository;
    private final OngRepository ongRepository;
    private final AnimalRepository animalRepository;

    public AdoptionRequestService(AdoptionRequestRepository adoptionRequestRepository,
                                  AdopterRepository adopterRepository,
                                  OngRepository ongRepository,
                                  AnimalRepository animalRepository) {
        this.adoptionRequestRepository = adoptionRequestRepository;
        this.adopterRepository = adopterRepository;
        this.ongRepository = ongRepository;
        this.animalRepository = animalRepository;
    }

    @Transactional
    public SolicitacaoAdocaoResponseDTO criarSolicitacao(Long adopterId, String tipoUsuario, CriarSolicitacaoAdocaoDTO dto) {
        if (!"ADOTANTE".equalsIgnoreCase(tipoUsuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas adotantes podem solicitar adoção.");
        }

        AdopterEntity adotante = adopterRepository.findById(adopterId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Adotante não encontrado."));

        AnimalEntity animal = animalRepository.findById(dto.animalId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Animal não encontrado."));

        if (Boolean.TRUE.equals(animal.getAdoptionStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este animal já foi adotado.");
        }

        if (adoptionRequestRepository.existsByAdopter_IdAndAnimal_IdAndStatus(
                adopterId, animal.getId(), StatusAdocaoEnum.PENDENTE)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Você já possui uma solicitação pendente para este animal.");
        }

        // Busca a ONG vinculada ao animal (ajuste conforme o campo da sua AnimalEntity, ex: cnpjId ou ong)
        OngEntity ong = ongRepository.findById(animal.getOng().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ONG responsável pelo animal não encontrada."));

        AdoptionRequestEntity solicitacao = new AdoptionRequestEntity();
        solicitacao.setAdopter(adotante);
        solicitacao.setOng(ong);
        solicitacao.setAnimal(animal);
        solicitacao.setStatus(StatusAdocaoEnum.PENDENTE);
        solicitacao.setMensagemAdotante(dto.mensagemAdotante());
        solicitacao.setDataSolicitacao(LocalDateTime.now());

        AdoptionRequestEntity salva = adoptionRequestRepository.save(solicitacao);
        return SolicitacaoAdocaoResponseDTO.fromEntity(salva);
    }

    @Transactional
    public SolicitacaoAdocaoResponseDTO responderSolicitacao(Long solicitacaoId,
                                                             Long ongId,
                                                             String tipoUsuario,
                                                             ResponderSolicitacaoAdocaoDTO dto) {
        if (!"ONG".equalsIgnoreCase(tipoUsuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas ONGs podem aprovar ou reprovar solicitações.");
        }

        AdoptionRequestEntity solicitacao = adoptionRequestRepository.findById(solicitacaoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitação de adoção não encontrada."));

        if (!solicitacao.getOng().getId().equals(ongId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para responder solicitações de outra ONG.");
        }

        if (solicitacao.getStatus() != StatusAdocaoEnum.PENDENTE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Esta solicitação já foi respondida.");
        }

        solicitacao.setStatus(dto.aprovar() ? StatusAdocaoEnum.APROVADA : StatusAdocaoEnum.REJEITADA);
        solicitacao.setRespostaOng(dto.respostaOng());

        if (dto.aprovar()) {
            AnimalEntity animal = solicitacao.getAnimal();
            animal.setAdoptionStatus(true);
            animalRepository.save(animal);
        }

        AdoptionRequestEntity atualizada = adoptionRequestRepository.save(solicitacao);
        return SolicitacaoAdocaoResponseDTO.fromEntity(atualizada);
    }

    public List<SolicitacaoAdocaoResponseDTO> listarPorAdotante(Long adopterId) {
        return adoptionRequestRepository.findByAdopter_IdOrderByDataSolicitacaoDesc(adopterId)
                .stream()
                .map(SolicitacaoAdocaoResponseDTO::fromEntity)
                .toList();
    }

    public List<SolicitacaoAdocaoResponseDTO> listarPorOng(Long ongId) {
        return adoptionRequestRepository.findByOng_IdOrderByDataSolicitacaoDesc(ongId)
                .stream()
                .map(SolicitacaoAdocaoResponseDTO::fromEntity)
                .toList();
    }
}