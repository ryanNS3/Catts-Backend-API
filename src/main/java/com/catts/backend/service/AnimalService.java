package com.catts.backend.service;

import com.catts.backend.database.model.AnimalEntity;
import com.catts.backend.dto.AnimalDTO;
import com.catts.backend.repository.AnimalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@RequiredArgsConstructor // 2. Adicione esta anotação na classe
@Service
public class AnimalService {
    private final AnimalRepository animalRepository;
    // buscar todos os animais
    public List<AnimalEntity> findALL(){
        return animalRepository.findAll();
    }

//    public AnimalEntity createAnimal(AnimalDTO animal){
//        AnimalEntity newAnimal = new AnimalEntity(animal.getIdOng(), animal.getName(), animal.getAge(), animal.getWeight(), animal.getDescription(), animal.getAdoptionStatus(), animal.getCastrado(), animal.getVaccine());
//        animais.add(newAnimal);
//        return newAnimal;
//    }


}
