package com.catts.backend.repository;

import com.catts.backend.database.model.AdopterEntity;
import com.catts.backend.database.model.AnimalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnimalRepository extends JpaRepository<AnimalEntity, String> {
    // Busca todos os animais vinculados ao ID numérico da OngEntity
    List<AnimalEntity> findByOngId(Long ongId);

}