package com.pewniaczekbet.dto;

/**
 * GameScoreChangeDto
 */
public class GameScoreChangeDto {
	private Long gameId;
	private Long team1Score;
	private Long team2Score;

	public Long getGameId() {
		return gameId;
	}

	public void setGameId(Long gameId) {
		this.gameId = gameId;
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
