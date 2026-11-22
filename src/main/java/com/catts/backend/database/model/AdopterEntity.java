package com.catts.backend.database.model;

import com.catts.backend.enums.TipoResidenciaEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AdopterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome; // Importante ter para guardar o nome vindo do Google
    private String email; // Vem do google tbm
    private String contato;
    private String cpf;
    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    private TipoResidenciaEnum tipoResidencia;

    private Boolean possuiTelaProtecao;
    private Boolean imovelAlugado;
    private Float salario;
    private String quantidadePet;
    private Integer quantidadeHorasTrabalho;
    private Integer quantidadePessoasCasa;
    private String descricao;

    // Cria a chave estrangeira 'address_id' na tabela de adotantes
    // CascadeType.ALL salva/atualiza o endereço automaticamente junto com o adotante
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private AddressEntity address;
}