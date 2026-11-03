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

@RequiredArgsConstructor // 2. Adicione esta anotação na classe
@Service
public class AnimalService {
    private final AnimalRepository animalRepository;
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


}
