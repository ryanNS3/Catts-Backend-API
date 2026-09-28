package com.catts.backend.service;

import com.catts.backend.database.model.AnimalEntity;
import com.catts.backend.dto.AnimalDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnimalService {
    private static final List<AnimalEntity> animais = new ArrayList<AnimalEntity>();

    // buscar todos os animais
    public List<AnimalEntity> findALL(){
        return new ArrayList<>(animais);
    }

    public AnimalEntity createAnimal(AnimalDTO animal){
        AnimalEntity newAnimal = new AnimalEntity(1, "malu", 15, "pequeno", "ee", false, "sp");
        animais.add(newAnimal);
        return newAnimal;
    }


}
