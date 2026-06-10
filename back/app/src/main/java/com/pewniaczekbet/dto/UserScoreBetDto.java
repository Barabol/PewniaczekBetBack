package com.pewniaczekbet.dto;

import com.pewniaczekbet.model.entities.UserScoreBetEntity;

/**
 * UserScoreBetDto
 */
public class UserScoreBetDto {
	private UserDto user;
	private ScoreBetDto bet;
	private Long team1Score;
	private Long team2Score;
	private Double multiplyer;
	private Long ammount;

	public static UserScoreBetDto fromEntity(UserScoreBetEntity entity) {
		UserScoreBetDto dto = new UserScoreBetDto();
		dto.setUser(UserDto.fromEntity(entity.getUser()));
		dto.setBet(ScoreBetDto.fromEntity(entity.getBet()));
		dto.setAmmount(entity.getAmmount());
		dto.setMultiplyer(entity.getMultiplyer());
		dto.setTeam1Score(entity.getTeam1Score());
		dto.setTeam2Score(entity.getTeam2Score());
		return dto;
	}

	public UserDto getUser() {
		return user;
	}

	public void setUser(UserDto user) {
		this.user = user;
	}

	public ScoreBetDto getBet() {
		return bet;
	}

	public void setBet(ScoreBetDto bet) {
		this.bet = bet;
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

	public Double getMultiplyer() {
		return multiplyer;
	}

	public void setMultiplyer(Double multiplyer) {
		this.multiplyer = multiplyer;
	}

	public Long getAmmount() {
		return ammount;
	}

	public void setAmmount(Long ammount) {
		this.ammount = ammount;
	}

}
