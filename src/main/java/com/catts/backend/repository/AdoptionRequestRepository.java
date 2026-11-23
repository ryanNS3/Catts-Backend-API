package com.catts.backend.repository;

import com.catts.backend.database.model.AdoptionRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdoptionRequestRepository extends JpaRepository<AdoptionRequestEntity, Long> {
    boolean existsByAnimalId(String animalId);
    void deleteByAnimalId(String animalId);
}
