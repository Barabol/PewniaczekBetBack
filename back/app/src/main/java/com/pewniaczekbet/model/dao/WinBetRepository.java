package com.pewniaczekbet.model.dao;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pewniaczekbet.model.entities.WinBetEntity;

public interface WinBetRepository extends JpaRepository<WinBetEntity, Long> {
	Page<WinBetEntity> findAll(Pageable pageable);

	Page<WinBetEntity> findByStopDateAfter(LocalDateTime date, Pageable pageable);

	Page<WinBetEntity> findByStopDateAfterAndGameSportName(LocalDateTime date, String sport, Pageable pageable);

	Page<WinBetEntity> findByGameSportName(String sport, Pageable pageable);
}
