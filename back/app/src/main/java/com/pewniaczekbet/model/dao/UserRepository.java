package com.pewniaczekbet.model.dao;

import com.pewniaczekbet.model.entities.UserEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
	UserEntity findByEmail(String email);

	Page<UserEntity> findByIsPublicTrue(Pageable pageable);
}
