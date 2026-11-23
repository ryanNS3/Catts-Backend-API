package com.catts.backend.repository;

import com.catts.backend.database.model.AdopterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdopterRepository extends JpaRepository<AdopterEntity, Long> {
    Optional<AdopterEntity> findByEmail(String email);

}
