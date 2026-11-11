package com.catts.backend.service;

import com.catts.backend.database.model.AnimalEntity;
import com.catts.backend.database.model.OngEntity;
import com.catts.backend.database.model.PhotoAnimalEntity;
import com.catts.backend.dto.AnimalDTO;
import com.catts.backend.dto.AnimalResponseDTO;
import com.catts.backend.dto.OngResponseDTO;
import com.catts.backend.dto.PhotoResponseDTO;
import com.catts.backend.repository.AnimalRepository;
import com.catts.backend.repository.OngRepository;
import com.catts.backend.repository.PhotoAnimalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final OngRepository ongRepository;
    private final PhotoAnimalRepository photoAnimalRepository;
    private final AzureBlobService azureBlobService;


    public List<AnimalResponseDTO> findALL() {
        return animalRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public AnimalResponseDTO createAnimal(AnimalDTO dto, MultipartFile arquivo) throws IOException {

        if (dto.getCnpjOng() == null || dto.getCnpjOng().isBlank()) {
            throw new IllegalArgumentException("O CNPJ da ONG é obrigatório.");
        }
        if (dto.getNome() == null || dto.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome do animal é obrigatório.");
        }
        if (dto.getPorte() != null && dto.getPorte().length() > 10) {
            throw new IllegalArgumentException("O porte deve ter no máximo 10 caracteres.");
        }
        if (dto.getDescricao() != null && dto.getDescricao().length() > 100) {
            throw new IllegalArgumentException("A descrição deve ter no máximo 100 caracteres.");
        }
        if (dto.getLocalizacao() != null && dto.getLocalizacao().length() > 50) {
            throw new IllegalArgumentException("A localização deve ter no máximo 50 caracteres.");
        }

        OngEntity ong = ongRepository.findById(dto.getCnpjOng())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "ONG não encontrada."));

        String blobName = azureBlobService.uploadImagemAnimal(arquivo);

        try {
            AnimalEntity animal = new AnimalEntity();
            animal.setId(UUID.randomUUID().toString());   // id gerado aqui, a coluna é varchar
            animal.setOng(ong);
            animal.setName(dto.getNome());
            animal.setAge(dto.getIdade());
            animal.setPorte(dto.getPorte());
            animal.setDescription(dto.getDescricao());
            animal.setLocalizacao(dto.getLocalizacao());
            animal.setAdoptionStatus(dto.getStatusAdocao() != null ? dto.getStatusAdocao() : true);
            animal = animalRepository.saveAndFlush(animal);

            PhotoAnimalEntity photo = new PhotoAnimalEntity(blobName, animal);
            photoAnimalRepository.saveAndFlush(photo);
            animal.setImage(photo);

            return toResponse(animal);

        } catch (RuntimeException e) {
            // Se o banco falhou, apaga a foto do Blob para não ficar sozinha la, e n repassa o erro
            azureBlobService.deleteBlob(blobName);
            throw e;
        }
    }

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