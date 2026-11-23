package com.catts.backend.controller;

import com.catts.backend.database.model.OngEntity;
import com.catts.backend.dto.AnimalDTO;
import com.catts.backend.dto.AnimalResponseDTO;
import com.catts.backend.dto.AnimalUpdateDTO;
import com.catts.backend.repository.OngRepository;
import com.catts.backend.service.AnimalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

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

    // retorna os animais de acordo com id da ong
    @GetMapping("/ong/{ongId}")
    public ResponseEntity<List<AnimalResponseDTO>> listarPorOng(@PathVariable Long ongId) {
        return ResponseEntity.ok(animalService.findAnimalByOngId(ongId));
    }

    // 4. Atualizar animal (apenas ONG dona do animal)
    @PutMapping("/{id}")
    public ResponseEntity<AnimalResponseDTO> atualizarAnimal(
            @PathVariable("id") String id,
            @RequestBody AnimalUpdateDTO dto
    ) {
        OngEntity ongLogada = buscarOngLogada();
        AnimalResponseDTO atualizado = animalService.atualizarAnimal(id, dto, ongLogada);
        return ResponseEntity.ok(atualizado);
    }

    // 5. Deletar animal (apenas ONG dona do animal)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarAnimal(@PathVariable("id") String id) {
        OngEntity ongLogada = buscarOngLogada();
        animalService.deletarAnimal(id, ongLogada);
    }

    private OngEntity buscarOngLogada() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado.");
        }

        return animalService.buscarOngPorEmail(auth.getName());
    }


}
