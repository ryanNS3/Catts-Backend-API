package com.catts.backend.dto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnimalUpdateDTO {
    private String name;
    private Integer age;
    private Integer weight;
    private String description;
    private String porte;
    private String localizacao;
    private Boolean adoptionStatus;
    private Boolean castrado;
    private String[] vaccine;
    private String imageUrl;
}
