package com.catts.backend.repository;

import com.catts.backend.database.model.PhotoAnimalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoAnimalRepository extends JpaRepository<PhotoAnimalEntity, String> {
}