package com.catts.backend.controller;

import com.catts.backend.dto.AnimalDTO;
import com.catts.backend.dto.AnimalResponseDTO;
import com.catts.backend.service.AnimalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v1/animal")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AnimalController {
    private final AnimalService animalService;

    // retorna todos os animais
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AnimalResponseDTO> allAnimal() {
        return animalService.findALL();
    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public AnimalResponseDTO createAnimal(
            @ModelAttribute AnimalDTO animal,
            @RequestParam("arquivo") MultipartFile arquivo
    ) throws IOException {
        return animalService.createAnimal(animal, arquivo);
    }


    @GetMapping("/adoption")
    @ResponseStatus(HttpStatus.OK)
    public String getAnimalToAdoption() {
        return "Todos os animais para adoção";
    }


    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String getAnimalById(@PathVariable("id") String id) {
        return "Animal : " + id;
    }
}