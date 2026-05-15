package com.pewniaczekbet.model.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pewniaczekbet.model.entities.UserWinBetEntity;

public interface UserWinBetRepository extends JpaRepository<UserWinBetEntity, Long> {
	Page<UserWinBetEntity> findByUserId(Long userId, Pageable pageable);
}
