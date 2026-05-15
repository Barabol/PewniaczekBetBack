package com.pewniaczekbet.model.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pewniaczekbet.model.entities.UserScoreBetEntity;

public interface UserScoreBetRepository extends JpaRepository<UserScoreBetEntity, Long> {
	Page<UserScoreBetEntity> findByUserId(Long userId, Pageable pageable);
}
