package com.pewniaczekbet.model.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pewniaczekbet.model.entities.UserWinBetEntity;

public interface UserWinBetRepository extends JpaRepository<UserWinBetEntity, Long> {
	Page<UserWinBetEntity> findByUserId(Long userId, Pageable pageable);

	Page<UserWinBetEntity> findAllByUserId(Long userId, Pageable pageable);

	Page<UserWinBetEntity> findAllByUserIdAndBetStopDateAfterOrderByIdDesc(Long userId, LocalDateTime date,
			Pageable pageable);

	Page<UserWinBetEntity> findAllByUserIdAndBetGameSportNameOrderByIdDesc(Long userId,
			String sport, Pageable pageable);

	Page<UserWinBetEntity> findAllByUserIdAndBetStopDateBeforeOrderByIdDesc(Long userId,
			LocalDateTime date, Pageable pageable);

	Page<UserWinBetEntity> findAllByUserIdAndBetGameSportNameAndBetStopDateAfterOrderByIdDesc(Long userId,
			String sport, LocalDateTime date, Pageable pageable);

	Page<UserWinBetEntity> findAllByUserIdAndBetGameSportNameAndBetStopDateBeforeOrderByIdDesc(Long userId,
			String sport, LocalDateTime date, Pageable pageable);

	List<UserWinBetEntity> findAllByBetId(Long betId);
}
