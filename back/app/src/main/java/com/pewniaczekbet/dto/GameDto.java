package com.pewniaczekbet.dto;

import java.time.LocalDateTime;
import com.pewniaczekbet.model.entities.GameEntity;

/**
 * GameDto
 */
public class GameDto {
	private Long id;
	private String name;
	private LocalDateTime startDate;
	private String team1;
	private String team2;
	private String sport;
	private Long team1Score;
	private Long team2Score;

	public static GameDto fromEntity(GameEntity entity) {
		GameDto dto = new GameDto();
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setStartDate(entity.getStartDate());
		dto.setTeam1(entity.getTeam1().getName());
		dto.setTeam2(entity.getTeam2().getName());
		dto.setSport(entity.getSport().getName());
		dto.setTeam1Score(entity.getTeam1Score());
		dto.setTeam2Score(entity.getTeam2Score());
		return dto;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public String getTeam1() {
		return team1;
	}

	public void setTeam1(String team1) {
		this.team1 = team1;
	}

	public String getTeam2() {
		return team2;
	}

	public void setTeam2(String team2) {
		this.team2 = team2;
	}

	public String getSport() {
		return sport;
	}

	public void setSport(String sport) {
		this.sport = sport;
	}

	public Long getTeam1Score() {
		return team1Score;
	}

	public void setTeam1Score(Long team1Score) {
		this.team1Score = team1Score;
	}

	public Long getTeam2Score() {
		return team2Score;
	}

	public void setTeam2Score(Long team2Score) {
		this.team2Score = team2Score;
	}

}
