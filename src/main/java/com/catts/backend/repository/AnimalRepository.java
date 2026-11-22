package com.catts.backend.repository;

import com.catts.backend.database.model.AdopterEntity;
import com.catts.backend.database.model.AnimalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnimalRepository extends JpaRepository<AnimalEntity, String> {

}