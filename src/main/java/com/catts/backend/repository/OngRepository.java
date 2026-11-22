package com.catts.backend.repository;

import com.catts.backend.database.model.AdopterEntity;
import com.catts.backend.database.model.OngEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OngRepository extends JpaRepository<OngEntity, String> {
    Optional<OngEntity> findByEmail(String email);

}
