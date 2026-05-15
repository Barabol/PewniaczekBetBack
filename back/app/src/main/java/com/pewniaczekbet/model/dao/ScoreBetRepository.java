package com.pewniaczekbet.model.dao;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pewniaczekbet.model.entities.ScoreBetEntity;

public interface ScoreBetRepository extends JpaRepository<ScoreBetEntity, Long> {
	Page<ScoreBetEntity> findAll(Pageable pageable);

	Page<ScoreBetEntity> findByStopDateAfter(LocalDateTime date, Pageable pageable);

	Page<ScoreBetEntity> findByStopDateAfterAndGameSportName(LocalDateTime date, String sport, Pageable pageable);

	Page<ScoreBetEntity> findByGameSportName(String sport, Pageable pageable);
}
