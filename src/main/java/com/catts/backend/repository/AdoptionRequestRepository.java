package com.catts.backend.repository;

import com.catts.backend.database.model.AdoptionRequestEntity;
import com.catts.backend.enums.StatusAdocaoEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdoptionRequestRepository extends JpaRepository<AdoptionRequestEntity, Long> {
    boolean existsByAnimalId(String animalId);
    void deleteByAnimalId(String animalId);
    List<AdoptionRequestEntity> findByAdopter_IdOrderByDataSolicitacaoDesc(Long adopterId);

    List<AdoptionRequestEntity> findByOng_IdOrderByDataSolicitacaoDesc(Long ongId);

    boolean existsByAdopter_IdAndAnimal_IdAndStatus(Long adopterId, String animalId, StatusAdocaoEnum status);
}

