package com.catts.backend.dto;

import com.catts.backend.database.model.AnimalEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AnimalDTO {

    private String id;
    private Long idOng;
    private String name;
    private Integer age;
    private Integer weight;
    private String description;
    private Boolean adoptionStatus;
    private Boolean castrado;
    private String[] vaccine;
    private String cnpjOng;       // CNPJ da ONG dona do animal (mesma formatacao do banco)
    private String porte;
    private String localizacao;
    private String imageUrl;

    // Método estático que converte uma AnimalEntity do banco para o formato exato do AnimalDTO
    public static AnimalDTO fromEntity(AnimalEntity entity) {
        AnimalDTO dto = new AnimalDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setAge(entity.getAge());
        dto.setWeight(entity.getWeight());
        dto.setDescription(entity.getDescription());
        dto.setAdoptionStatus(entity.getAdoptionStatus());
        dto.setCastrado(entity.getCastrado());
        dto.setPorte(entity.getPorte());
        dto.setLocalizacao(entity.getLocalizacao());

        // Extrai o ID e o CNPJ da ONG vinculada (se existir)
        if (entity.getOng() != null) {
            dto.setIdOng(entity.getOng().getId());
            dto.setCnpjOng(entity.getOng().getCnpj());
        } else if (entity.getIdOng() != null) {
            dto.setIdOng(entity.getIdOng().longValue());
        }

        // Extrai o link da foto vinculada (se existir)
        if (entity.getImage() != null) {
            dto.setImageUrl(entity.getImage().getLinkId());
        }

        // Converte a String JSON '["Raiva", "V4"]' do banco para String[]
        dto.setVaccine(converterVacinas(entity.getVaccine()));

        return dto;
    }

    private static String[] converterVacinas(String vaccineJson) {
        if (vaccineJson == null || vaccineJson.isBlank()) {
            return new String[0];
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(vaccineJson, String[].class);
        } catch (JsonProcessingException e) {
            // Fallback caso o texto no banco não esteja em formato JSON array (ex: separado por vírgula)
            return vaccineJson.replace("[", "")
                    .replace("]", "")
                    .replace("\"", "")
                    .split("\\s*,\\s*");
        }
    }
}