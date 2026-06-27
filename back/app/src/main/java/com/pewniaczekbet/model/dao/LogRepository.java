package com.pewniaczekbet.model.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pewniaczekbet.model.entities.LogEntity;

/**
 * LogRepository
 */
public interface LogRepository extends JpaRepository<LogEntity, Long> {
	Page<LogEntity> findAll(Pageable pageable);
}
