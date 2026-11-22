package com.catts.backend.database.model;

import com.catts.backend.enums.StatusAdocaoEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "adoption_request_entity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AdoptionRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "adopter_id", nullable = false)
    private AdopterEntity adopter;

    @ManyToOne
    @JoinColumn(name = "ong_id", nullable = false)
    private OngEntity ong;

    @ManyToOne
    @JoinColumn(name = "animal_id", nullable = false)
    private AnimalEntity animal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAdocaoEnum status = StatusAdocaoEnum.PENDENTE;

    @Column(name = "mensagem_adotante", length = 1000)
    private String mensagemAdotante;

    @Column(name = "resposta_ong", length = 1000)
    private String respostaOng;

    @Column(name = "data_solicitacao", nullable = false)
    private LocalDateTime dataSolicitacao = LocalDateTime.now();
}