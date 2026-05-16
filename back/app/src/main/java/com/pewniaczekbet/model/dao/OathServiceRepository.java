
package com.pewniaczekbet.model.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pewniaczekbet.model.entities.OathServiceEntity;

public interface OathServiceRepository extends JpaRepository<OathServiceEntity, Long> {
	Optional<OathServiceEntity> findByName(String Name);
}
