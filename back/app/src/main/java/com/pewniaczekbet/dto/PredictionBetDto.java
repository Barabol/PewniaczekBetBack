package com.pewniaczekbet.dto;

import java.time.LocalDateTime;

import com.pewniaczekbet.model.entities.PredictionBetEntity;

/**
 * PredictionBetDto
 */
public class PredictionBetDto {
	private Long id;
	private String name;
	private Double currentMultiplier;
	private LocalDateTime startDate;
	private LocalDateTime stopDate;
	private Long trueBets;
	private Long falseBets;
	private Long trueBetsAmount;
	private Long falseBetsAmount;
	private Long pot;
	private Boolean endedWith;

	public static PredictionBetDto fromEntity(PredictionBetEntity entity) {
		PredictionBetDto dto = new PredictionBetDto();
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setCurrentMultiplier(entity.getCurrentMultiplier());
		dto.setStartDate(entity.getStartDate());
		dto.setStopDate(entity.getStopDate());
		dto.setTrueBets(entity.getTrueBets());
		dto.setFalseBets(entity.getFalseBets());
		dto.setPot(entity.getPot());
		dto.setEndedWith(entity.getEndedWith());
		dto.setTrueBetsAmount(entity.getTrueBetsAmount());
		dto.setFalseBetsAmount(entity.getFalseBetsAmount());
		return dto;
	}

	public PredictionBetEntity toEntity() {
		PredictionBetEntity entity = new PredictionBetEntity();
		entity.setName(this.getName());
		entity.setCurrentMultiplier(this.getCurrentMultiplier());
		entity.setStartDate(this.getStartDate());
		entity.setStopDate(this.getStopDate());
		entity.setTrueBets(0L);
		entity.setFalseBets(0L);
		entity.setTrueBetsAmount(0L);
		entity.setFalseBetsAmount(0L);
		entity.setPot(0L);
		entity.setEndedWith(null);
		entity.setPaid(false);
		return entity;
	}

	public Long getTrueBetsAmount() {
		return trueBetsAmount;
	}

	public void setTrueBetsAmount(Long trueBetsAmount) {
		this.trueBetsAmount = trueBetsAmount;
	}

	public Long getFalseBetsAmount() {
		return falseBetsAmount;
	}

	public void setFalseBetsAmount(Long falseBetsAmount) {
		this.falseBetsAmount = falseBetsAmount;
	}

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
