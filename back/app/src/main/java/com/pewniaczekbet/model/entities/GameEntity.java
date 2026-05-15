package com.pewniaczekbet.model.entities;

import java.time.LocalDateTime;

import jakarta.persistence.*;

/**
 * GameEntity
 */
@Entity
@Table(name = "games")
public class GameEntity {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "team1_id")
	private TeamEntity team1;

	@ManyToOne
	@JoinColumn(name = "team2_id")
	private TeamEntity team2;

	@ManyToOne
	@JoinColumn(name = "sport_id")
	private SportEntity sport;

	@Column(name = "team1_score")
	private Long team1Score;

	@Column(name = "team2_score")
	private Long team2Score;

	@Column(name = "start_date")
	private LocalDateTime startDate;

	@Column(name = "name")
	private String name;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TeamEntity getTeam1() {
		return team1;
	}

	public void setTeam1(TeamEntity team1) {
		this.team1 = team1;
	}

	public TeamEntity getTeam2() {
		return team2;
	}

	public void setTeam2(TeamEntity team2) {
		this.team2 = team2;
	}

	public SportEntity getSport() {
		return sport;
	}

	public void setSport(SportEntity sport) {
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

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
