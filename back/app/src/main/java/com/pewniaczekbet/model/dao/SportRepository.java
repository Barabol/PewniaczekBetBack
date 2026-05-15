package com.pewniaczekbet.model.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pewniaczekbet.model.entities.SportEntity;

public interface SportRepository extends JpaRepository<SportEntity, Long> {
	Optional<SportEntity> findByName(String name);
}
