package com.pewniaczekbet.model.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pewniaczekbet.model.entities.TeamEntity;

public interface TeamRepository extends JpaRepository<TeamEntity, Long> {
	Optional<TeamEntity> findByName(String name);
}
