package com.catts.backend.database.model;

import com.catts.backend.enums.TipoResidenciaEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "adopter_entity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AdopterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String contato;
    private String cpf;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_residencia")
    private TipoResidenciaEnum tipoResidencia;

    @Column(name = "possui_tela_protecao")
    private Boolean possuiTelaProtecao;

    @Column(name = "imovel_alugado")
    private Boolean imovelAlugado;

    private Float salario;

    @Column(name = "quantidade_pet")
    private String quantidadePet;

    @Column(name = "quantidade_horas_trabalho")
    private String quantidadeHorasTrabalho;

    @Column(name = "quantidade_pessoas_casa")
    private Integer quantidadePessoasCasa;

    private String descricao;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private AddressEntity address;
}