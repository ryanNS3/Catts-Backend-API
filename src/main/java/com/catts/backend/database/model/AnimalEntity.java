package com.catts.backend.database.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AnimalEntity {
    @Id
    private String id;
    private Integer idOng;
    private String nome;
    private Integer idade;
    private String porte;
    private String descricao;
    private Boolean statusAdocao;
    private String localizacao;


    public AnimalEntity(int i, String malu, int i1, String pequeno, String ee, boolean b, String sp) {
    }
}
