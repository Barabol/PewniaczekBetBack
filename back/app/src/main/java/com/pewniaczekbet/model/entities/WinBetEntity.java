package com.pewniaczekbet.model.entities;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "win_bets")
public class WinBetEntity {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "name")
	private String name;
	@Column(name = "curent_multiplyer")
	private Double currentMultiplier;
	@Column(name = "stop_date")
	private LocalDateTime stopDate;

	@ManyToOne
	@JoinColumn(name = "game_id")
	private GameEntity game;

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

	public Double getCurrentMultiplier() {
		return currentMultiplier;
	}

	public void setCurrentMultiplier(Double currentMultiplier) {
		this.currentMultiplier = currentMultiplier;
	}

	public LocalDateTime getStopDate() {
		return stopDate;
	}

	public void setStopDate(LocalDateTime stopDate) {
		this.stopDate = stopDate;
	}

	public GameEntity getGame() {
		return game;
	}

	public void setGame(GameEntity game) {
		this.game = game;
	}
}
