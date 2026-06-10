package com.pewniaczekbet.dto;

import com.pewniaczekbet.model.entities.UserWinBetEntity;

/**
 * UserWinBetDto
 */
public class UserWinBetDto {
	private UserDto user;
	private WinBetDto bet;
	private String team;
	private Double multiplyer;
	private Long amount;

	public static UserWinBetDto fromEntity(UserWinBetEntity entity) {
		UserWinBetDto dto = new UserWinBetDto();
		dto.setUser(UserDto.fromEntity(entity.getUser()));
		dto.setTeam(entity.getTeam().getName());
		dto.setMultiplyer(entity.getMultiplyer());
		dto.setAmount(entity.getAmmount());
		dto.setBet(WinBetDto.fromEntity(entity.getBet()));
		return dto;
	}

	public UserDto getUser() {
		return user;
	}

	public void setUser(UserDto user) {
		this.user = user;
	}

	public WinBetDto getBet() {
		return bet;
	}

	public void setBet(WinBetDto bet) {
		this.bet = bet;
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public Double getMultiplyer() {
		return multiplyer;
	}

	public void setMultiplyer(Double multiplyer) {
		this.multiplyer = multiplyer;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}
}
