package com.pewniaczekbet.dto;

/**
 * ScoreBetPlaceDto
 */
public class ScoreBetPlaceDto {
	private Long betId;
	private Long ammount;
	private Boolean isFreeBet;
	private Long team1Score;
	private Long team2Score;

	public Boolean getIsFreeBet() {
		return isFreeBet;
	}

	public void setIsFreeBet(Boolean isFreeBet) {
		this.isFreeBet = isFreeBet;
	}

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
