package com.catts.backend.service;

import com.catts.backend.database.model.AnimalEntity;
import com.catts.backend.dto.AnimalDTO;
import com.catts.backend.dto.AnimalResponseDTO;
import com.catts.backend.dto.OngResponseDTO;
import com.catts.backend.dto.PhotoResponseDTO;
import com.catts.backend.repository.AnimalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    // buscar todos os animais
// Buscar todos os animais convertendo para DTO (Elimina o looping infinito)
    public List<AnimalResponseDTO> findALL() {
        List<AnimalEntity> animais = animalRepository.findAll();

        return animais.stream().map(animal -> {
            OngResponseDTO ongDto = null;
            if (animal.getOng() != null) {
                ongDto = OngResponseDTO.builder()
                        .cnpj(animal.getOng().getCNPJ())
                        .nomeInstitucional(animal.getOng().getNomeInstitucional())
                        .email(animal.getOng().getEmail())
                        .telefone(animal.getOng().getTelefone())
                        .descricao(animal.getOng().getDescricao())
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
//    public AnimalEntity createAnimal(AnimalDTO animal){
//        AnimalEntity newAnimal = new AnimalEntity(animal.getIdOng(), animal.getName(), animal.getAge(), animal.getWeight(), animal.getDescription(), animal.getAdoptionStatus(), animal.getCastrado(), animal.getVaccine());
//        animais.add(newAnimal);
//        return newAnimal;
//    }



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
        OngEntity ong = ongRepository.findById(dto.getCnpjOng())
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
                    .cnpj(animal.getOng().getCNPJ())
                    .nomeInstitucional(animal.getOng().getNomeInstitucional())
                    .email(animal.getOng().getEmail())
                    .telefone(animal.getOng().getTelefone())
                    .descricao(animal.getOng().getDescricao())
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
                .vaccine(animal.getVaccine())
                .ong(ongDto)
                .image(photoDto)
                .build();
    }
}
