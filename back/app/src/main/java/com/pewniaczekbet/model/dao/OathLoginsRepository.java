package com.pewniaczekbet.model.dao;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pewniaczekbet.model.entities.OathLoginsEntity;

/**
 * OathLoginsRepository
 */
public interface OathLoginsRepository extends JpaRepository<OathLoginsEntity, UUID> {
	Optional<OathLoginsEntity> findById(UUID id);
}
