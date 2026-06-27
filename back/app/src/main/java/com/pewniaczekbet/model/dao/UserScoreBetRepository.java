package com.pewniaczekbet.model.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pewniaczekbet.model.entities.UserScoreBetEntity;

public interface UserScoreBetRepository extends JpaRepository<UserScoreBetEntity, Long> {
	Page<UserScoreBetEntity> findByUserId(Long userId, Pageable pageable);

	Page<UserScoreBetEntity> findAllByUserId(Long userId, Pageable pageable);

	Page<UserScoreBetEntity> findAllByUserIdAndBetStopDateAfterOrderByIdDesc(Long userId, LocalDateTime date,
			Pageable pageable);

	Page<UserScoreBetEntity> findAllByUserIdAndBetGameSportNameOrderByIdDesc(Long userId,
			String sport, Pageable pageable);

	Page<UserScoreBetEntity> findAllByUserIdAndBetStopDateBeforeOrderByIdDesc(Long userId,
			LocalDateTime date, Pageable pageable);

	Page<UserScoreBetEntity> findAllByUserIdAndBetGameSportNameAndBetStopDateAfterOrderByIdDesc(Long userId,
			String sport, LocalDateTime date, Pageable pageable);

	Page<UserScoreBetEntity> findAllByUserIdAndBetGameSportNameAndBetStopDateBeforeOrderByIdDesc(Long userId,
			String sport, LocalDateTime date, Pageable pageable);

	List<UserScoreBetEntity> findAllByBetId(Long betId);
}
