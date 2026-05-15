package com.pewniaczekbet.model.entities;

import jakarta.persistence.*;

/**
 * UserWinBet
 */
@Entity
@Table(name = "user_win_bets")
public class UserWinBetEntity {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserEntity user;

	@ManyToOne
	@JoinColumn(name = "bet_id")
	private WinBetEntity bet;

	@ManyToOne
	@JoinColumn(name = "team_id")
	private TeamEntity team;

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

	public WinBetEntity getBet() {
		return bet;
	}

	public void setBet(WinBetEntity bet) {
		this.bet = bet;
	}

	public TeamEntity getTeam() {
		return team;
	}

	public void setTeam(TeamEntity team) {
		this.team = team;
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
