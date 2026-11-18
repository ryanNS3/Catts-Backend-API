package com.catts.backend.repository;

import com.catts.backend.database.model.AdopterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.ScopedValue;
import java.util.Optional;

public interface AdopterRepository extends JpaRepository<AdopterEntity, String> {
    Optional<AdopterEntity> findByEmail(String email);
}
