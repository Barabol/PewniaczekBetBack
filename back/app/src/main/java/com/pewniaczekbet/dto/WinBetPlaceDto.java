package com.pewniaczekbet.dto;

/**
 * WinBetPlaceDto
 */
public class WinBetPlaceDto {
	private Long betId;
	private Long ammount;
	private Boolean isFreeBet;
	private Boolean team;

	public Long getBetId() {
		return betId;
	}

	public void setBetId(Long betId) {
		this.betId = betId;
	}

	public Long getAmmount() {
		return ammount;
	}

	public void setAmmount(Long ammount) {
		this.ammount = ammount;
	}

	public Boolean getIsFreeBet() {
		return isFreeBet;
	}

	public void setIsFreeBet(Boolean isFreeBet) {
		this.isFreeBet = isFreeBet;
	}

	public Boolean getTeam() {
		return team;
	}

	public void setTeam(Boolean team) {
		this.team = team;
	}
}
