package com.catts.backend.controller;

import com.catts.backend.database.model.AnimalEntity;
import com.catts.backend.dto.AnimalDTO;
import com.catts.backend.dto.AnimalResponseDTO;
import com.catts.backend.service.AnimalService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/animal")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200") // <-- Libera o acesso para o Angular
public class AnimalController {
    private final AnimalService animalService;

    // retorna todos os animais
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AnimalResponseDTO> allAnimal(){
        return animalService.findALL();
    }

    // cadastra um animal com a foto (formulario multipart)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public AnimalResponseDTO createAnimal(
            @ModelAttribute AnimalDTO animal,               // campos de texto -> AnimalDTO
            @RequestParam("arquivo") MultipartFile arquivo  // a foto
    ) throws IOException {
        return animalService.createAnimal(animal, arquivo);
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




}
