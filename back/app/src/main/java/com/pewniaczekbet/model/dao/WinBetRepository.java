package com.pewniaczekbet.model.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pewniaczekbet.dto.SportListDto;
import com.pewniaczekbet.model.entities.WinBetEntity;

public interface WinBetRepository extends JpaRepository<WinBetEntity, Long> {
	Page<WinBetEntity> findAll(Pageable pageable);

	Page<WinBetEntity> findByStopDateAfter(LocalDateTime date, Pageable pageable);

	Page<WinBetEntity> findByStopDateAfterAndGameSportName(LocalDateTime date, String sport, Pageable pageable);

	Page<WinBetEntity> findByGameSportName(String sport, Pageable pageable);

	@Query("""
			 SELECT new com.pewniaczekbet.dto.SportListDto(
			     s.name as sportName,
			     COUNT(w) as count
			 )
			 FROM SportEntity s
			 LEFT JOIN GameEntity g ON g.sport = s
			 LEFT JOIN WinBetEntity w ON w.game = g
			 WHERE w.stopDate > :date OR w.id IS NULL
			 GROUP BY s.name
			""")
	List<SportListDto> countBySportName(@Param("date") LocalDateTime date);

	List<WinBetEntity> findByStopDateBeforeAndPaidFalse(LocalDateTime date);
}
