package com.pewniaczekbet.model.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pewniaczekbet.model.entities.UserPredictionBetEntity;

public interface UserPredictionBetRepository extends JpaRepository<UserPredictionBetEntity, Long> {
	Page<UserPredictionBetEntity> findAllByUserIdOrderByIdDesc(Long userId, Pageable pageable);

	Page<UserPredictionBetEntity> findAllByUserIdAndPredictionEndedWithIsNotNullOrderByIdDesc(Long userId,
			Pageable pageable);

	Page<UserPredictionBetEntity> findAllByUserIdAndPredictionEndedWithIsNullOrderByIdDesc(Long userId,
			Pageable pageable);
}
