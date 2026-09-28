package com.catts.backend.controller;

import com.catts.backend.database.model.AnimalEntity;
import com.catts.backend.dto.AnimalDTO;
import com.catts.backend.service.AnimalService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/animal")
@RequiredArgsConstructor
public class AnimalController {
    private final AnimalService animalService;

    // retorna todos os animais
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AnimalEntity> allAnimal(){
        return animalService.findALL();
    }

    // retorna todos os animais para adoção
    @GetMapping("/adoption")
    @ResponseStatus(HttpStatus.OK)
    public String getAnimalToAdoption(){
        return "Todos os animais para adoção";
    }

    // retorna um animal por id
    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String getAnimalById(@PathVariable("id") String id){
        return "Animal : " + id;

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AnimalDTO createAnimal(AnimalDTO animal) {

    }





}
