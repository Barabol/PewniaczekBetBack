package com.pewniaczekbet.model.entities;

import java.time.LocalDateTime;

import jakarta.persistence.*;

/**
 * PredictionBetEntity
 */
@Entity
@Table(name = "predictions")
public class PredictionBetEntity {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "name")
	private String name;
	@Column(name = "curent_multiplyer")
	private Double currentMultiplier;
	@Column(name = "start_date")
	private LocalDateTime startDate;
	@Column(name = "stop_date")
	private LocalDateTime stopDate;
	@Column(name = "true_bets")
	private Long trueBets;
	@Column(name = "false_bets")
	private Long falseBets;
	@Column(name = "pot")
	private Long pot;
	@Column(name = "ended_with")
	private Boolean endedWith;

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

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public LocalDateTime getStopDate() {
		return stopDate;
	}

	public void setStopDate(LocalDateTime stopDate) {
		this.stopDate = stopDate;
	}

	public Long getTrueBets() {
		return trueBets;
	}

	public void setTrueBets(Long trueBets) {
		this.trueBets = trueBets;
	}

	public Long getFalseBets() {
		return falseBets;
	}

	public void setFalseBets(Long falseBets) {
		this.falseBets = falseBets;
	}

	public Long getPot() {
		return pot;
	}

	public void setPot(Long pot) {
		this.pot = pot;
	}

	public Boolean getEndedWith() {
		return endedWith;
	}

	public void setEndedWith(Boolean endedWith) {
		this.endedWith = endedWith;
	}

}
