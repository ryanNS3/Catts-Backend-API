package com.catts.backend.dto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AnimalDTO {
    private Integer idOng;
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



}
