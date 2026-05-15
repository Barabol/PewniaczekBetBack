package com.pewniaczekbet.model.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pewniaczekbet.model.entities.GameEntity;

public interface GameRepository extends JpaRepository<GameEntity, Long> {
	Optional<GameEntity> findByName(String name);
}
