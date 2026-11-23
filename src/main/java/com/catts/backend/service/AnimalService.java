package com.catts.backend.service;

import com.azure.json.implementation.jackson.core.JsonProcessingException;
import com.catts.backend.database.model.AnimalEntity;
import com.catts.backend.dto.*;
import com.catts.backend.repository.AdoptionRequestRepository;
import com.catts.backend.repository.AnimalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import com.catts.backend.database.model.OngEntity;
import com.catts.backend.database.model.PhotoAnimalEntity;
import com.catts.backend.repository.OngRepository;
import com.catts.backend.repository.PhotoAnimalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor // 2. Adicione esta anotação na classe
@Service
public class AnimalService {
    private final AnimalRepository animalRepository;
    private final OngRepository ongRepository;
    private final PhotoAnimalRepository photoAnimalRepository;
    private final AzureBlobService azureBlobService;
    private final AdoptionRequestRepository adoptionRequestRepository;
    // buscar todos os animais
// Buscar todos os animais convertendo para DTO (Elimina o looping infinito)
    public List<AnimalResponseDTO> findALL() {
        List<AnimalEntity> animais = animalRepository.findAll();

        return animais.stream().map(animal -> {
            OngResponseDTO ongDto = null;
            if (animal.getOng() != null) {
                ongDto = OngResponseDTO.builder()
                        .cnpj(animal.getOng().getCnpj())
                        .nomeInstitucional(animal.getOng().getNomeInstitucional())
                        .email(animal.getOng().getEmail())
                        .contato(animal.getOng().getContato())
                        .build();
            }

            PhotoResponseDTO photoDto = null;
            if (animal.getImage() != null) {
                photoDto = PhotoResponseDTO.builder()
                        .linkId(animal.getImage().getLinkId())
                        .build();
            }

            return AnimalResponseDTO.builder()
                    .id(animal.getId())
                    .idOng(animal.getIdOng())
                    .name(animal.getName())
                    .age(animal.getAge())
                    .porte(animal.getPorte())
                    .localizacao(animal.getLocalizacao())
                    .weight(animal.getWeight())
                    .description(animal.getDescription())
                    .adoptionStatus(animal.getAdoptionStatus())
                    .castrado(animal.getCastrado())
                    .vaccine(animal.getVaccine() != null ? String.join(", ", animal.getVaccine()) : null) // Ajuste conforme seu tipo
                    .ong(ongDto)
                    .image(photoDto)
                    .build();
        }).collect(Collectors.toList());
    }


    // Cadastra um animal + foto: sobe a foto no Blob e grava Animal e Photo no banco
    @Transactional   // animal e foto sao gravados juntos: se um falhar, o banco desfaz os dois
    public AnimalResponseDTO createAnimal(AnimalDTO dto, MultipartFile arquivo) throws IOException {

        // 1) Validacoes: erro de quem mandou os dados vira 400 (ApiExceptionHandler)
        if (dto.getCnpjOng() == null || dto.getCnpjOng().isBlank()) {
            throw new IllegalArgumentException("O CNPJ da ONG é obrigatório.");
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("O nome do animal é obrigatório.");
        }
        if (dto.getName().length() > 100) {
            throw new IllegalArgumentException("O nome deve ter no máximo 100 caracteres.");
        }
        if (dto.getPorte() != null && dto.getPorte().length() > 10) {
            throw new IllegalArgumentException("O porte deve ter no máximo 10 caracteres.");
        }
        if (dto.getLocalizacao() != null && dto.getLocalizacao().length() > 50) {
            throw new IllegalArgumentException("A localização deve ter no máximo 50 caracteres.");
        }

        // 2) Busca a ONG pelo CNPJ; se nao existir, 404
        OngEntity ong = ongRepository.findById(dto.getIdOng())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "ONG não encontrada."));

        // 3) Sobe a foto pro Blob (valida vazio e formato la dentro) e recebe "animais/uuid.jpg"
        String blobName = azureBlobService.uploadImagemAnimal(arquivo);

        try {
            // 4) Monta e grava o animal
            AnimalEntity animal = new AnimalEntity();
            animal.setId(UUID.randomUUID().toString());   // id gerado aqui, a coluna e varchar
            animal.setOng(ong);
            animal.setName(dto.getName());
            animal.setAge(dto.getAge());
            animal.setWeight(dto.getWeight());
            animal.setPorte(dto.getPorte());
            animal.setDescription(dto.getDescription());
            animal.setLocalizacao(dto.getLocalizacao());
            animal.setAdoptionStatus(dto.getAdoptionStatus() != null ? dto.getAdoptionStatus() : true);
            animal.setCastrado(dto.getCastrado());
            animal.setVaccine(dto.getVaccine() != null ? String.join(", ", dto.getVaccine()) : null);
            // saveAndFlush manda o INSERT na hora (dentro do try), para o catch conseguir limpar o Blob
            animal = animalRepository.saveAndFlush(animal);

            // 5) Grava a foto ligando o nome do blob ao animal
            PhotoAnimalEntity photo = new PhotoAnimalEntity(blobName, animal);
            photoAnimalRepository.saveAndFlush(photo);
            animal.setImage(photo);

            return toResponse(animal);

        } catch (RuntimeException e) {
            // Se o banco falhou, apaga a foto do Blob para nao ficar orfa, e repassa o erro
            azureBlobService.deleteBlob(blobName);
            throw e;
        }
    }

    // Converte UM animal para o DTO de resposta (usado pelo cadastro)
    private AnimalResponseDTO toResponse(AnimalEntity animal) {
        OngResponseDTO ongDto = null;
        if (animal.getOng() != null) {
            ongDto = OngResponseDTO.builder()
                    .cnpj(animal.getOng().getCnpj())
                    .nomeInstitucional(animal.getOng().getNomeInstitucional())
                    .email(animal.getOng().getEmail())
                    .contato(animal.getOng().getContato())
                    .build();
        }

        PhotoResponseDTO photoDto = null;
        if (animal.getImage() != null) {
            photoDto = PhotoResponseDTO.builder()
                    .linkId(animal.getImage().getLinkId())
                    .build();
        }

        return AnimalResponseDTO.builder()
                .id(animal.getId())
                .idOng(animal.getIdOng())
                .name(animal.getName())
                .age(animal.getAge())
                .porte(animal.getPorte())
                .localizacao(animal.getLocalizacao())
                .weight(animal.getWeight())
                .adoptionStatus(animal.getAdoptionStatus())
                .castrado(animal.getCastrado())
                .vaccine(animal.getVaccine())
                .ong(ongDto)
                .image(photoDto)
                .build();
    }

// Listar animais da ong
    public List<AnimalResponseDTO> findAnimalByOngId(Long ongId){
        // Verifica primeiro se a ONG existe para retornar 404 em vez de lista vazia enganosa
        if (!ongRepository.existsById(ongId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ONG não encontrada com o ID informado.");
        }

        return animalRepository.findByOngId(ongId)
                .stream()
                .map(AnimalResponseDTO::fromEntity)
                .toList();
    }


    // 3. MÉTODO AUXILIAR DE SEGURANÇA (VALIDAÇÃO DE POSSE)

    private AnimalEntity buscarEValidarDono(String animalId, OngEntity ongLogada) {
        if (ongLogada == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Apenas ONGs autenticadas podem realizar esta ação.");
        }

        AnimalEntity animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Animal não encontrado."));

        if (animal.getOng() == null || !animal.getOng().getId().equals(ongLogada.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para alterar ou excluir este animal.");
        }

        return animal;
    }

    @Transactional
    public AnimalResponseDTO atualizarAnimal(String animalId, AnimalUpdateDTO dto, OngEntity ongLogada) {
        //  Busca o animal no banco e valida se a ONG logada é realmente a dona dele
        AnimalEntity animal = buscarEValidarDono(animalId, ongLogada);

        // Atualização parcial (só altera os campos que vieram preenchidos no JSON)
        if (dto.getName() != null) animal.setName(dto.getName());
        if (dto.getAge() != null) animal.setAge(dto.getAge());
        if (dto.getWeight() != null) animal.setWeight(dto.getWeight());
        if (dto.getDescription() != null) animal.setDescription(dto.getDescription());
        if (dto.getPorte() != null) animal.setPorte(dto.getPorte());
        if (dto.getLocalizacao() != null) animal.setLocalizacao(dto.getLocalizacao());
        if (dto.getAdoptionStatus() != null) animal.setAdoptionStatus(dto.getAdoptionStatus());
        if (dto.getCastrado() != null) animal.setCastrado(dto.getCastrado());

        //  Converte o array de vacinas para String usando o mesmo padrão do seu createAnimal
        if (dto.getVaccine() != null) {
            animal.setVaccine(String.join(", ", dto.getVaccine()));
        }

        //  Troca a foto caso uma nova URL/blobName seja informada
        if (dto.getImageUrl() != null && !dto.getImageUrl().isBlank()) {
            PhotoAnimalEntity fotoAtual = animal.getImage();

            if (fotoAtual == null || !fotoAtual.getLinkId().equals(dto.getImageUrl())) {
                if (fotoAtual != null) {
                    // Desvincula do animal e apaga o registro antigo do banco antes de inserir o novo
                    animal.setImage(null);
                    photoAnimalRepository.delete(fotoAtual);
                    photoAnimalRepository.flush();
                }

                PhotoAnimalEntity novaFoto = new PhotoAnimalEntity(dto.getImageUrl(), animal);
                photoAnimalRepository.saveAndFlush(novaFoto);
                animal.setImage(novaFoto);
            }
        }

        // Salva as alterações e devolve no formato padrão AnimalResponseDTO
        AnimalEntity atualizado = animalRepository.save(animal);
        return AnimalResponseDTO.fromEntity(atualizado);
    }

    // Deletar animal
    @Transactional
    public void deletarAnimal(String animalId, OngEntity ongLogada) {
        // Busca o animal e garante que pertence à ONG logada
        AnimalEntity animal = buscarEValidarDono(animalId, ongLogada);

        // Guarda o nome do arquivo no Azure Blob antes de apagar do banco
        String blobName = (animal.getImage() != null) ? animal.getImage().getLinkId() : null;

        // Remove solicitações de adoção vinculadas a esse animal (evita erro de FK no SQL Server)
        if (adoptionRequestRepository.existsByAnimalId(animalId)) {
            adoptionRequestRepository.deleteByAnimalId(animalId);
        }

        // Apaga o animal (a foto na tabela photo_animal_entity vai junto pelo CascadeType.ALL)
        animalRepository.delete(animal);

        // Remove a imagem física lá no Azure Blob Storage para não deixar lixo na nuvem
        if (blobName != null && !blobName.isBlank()) {
            azureBlobService.deleteBlob(blobName);
        }
    }



}