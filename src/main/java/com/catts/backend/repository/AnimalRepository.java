package com.catts.backend.repository;

import com.catts.backend.database.model.AnimalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnimalRepository extends JpaRepository<AnimalEntity, String> {
}