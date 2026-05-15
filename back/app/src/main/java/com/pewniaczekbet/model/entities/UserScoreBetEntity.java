package com.pewniaczekbet.model.entities;

import jakarta.persistence.*;

/**
 * UserWinBet
 */
@Entity
@Table(name = "user_score_bets")
public class UserScoreBetEntity {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserEntity user;

	@ManyToOne
	@JoinColumn(name = "bet_id")
	private ScoreBetEntity bet;

	@Column(name = "team1_score")
	private Long team1Score;

	@Column(name = "team2_score")
	private Long team2Score;

	@Column(name = "multiplyer")
	private Double multiplyer;

	@Column(name = "amount")
	private Long ammount;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public ScoreBetEntity getBet() {
		return bet;
	}

	public void setBet(ScoreBetEntity bet) {
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
