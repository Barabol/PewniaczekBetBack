package com.pewniaczekbet.dto;

import com.pewniaczekbet.model.entities.UserPredictionBetEntity;

/**
 * UserBetPredictionDto
 */
public class UserBetPredictionDto {
	private UserDto user;
	private PredictionBetDto bet;
	private Boolean prediction;
	private Long amount;

	public static UserBetPredictionDto fromEntity(UserPredictionBetEntity entity) {
		UserBetPredictionDto dto = new UserBetPredictionDto();
		dto.setUser(UserDto.fromEntity(entity.getUser()));
		dto.setAmount(entity.getAmount());
		dto.setPrediction(entity.getPredicted());
		dto.setBet(PredictionBetDto.fromEntity(entity.getPrediction()));
		return dto;
	}

	public UserDto getUser() {
		return user;
	}

	public void setUser(UserDto user) {
		this.user = user;
	}

	public PredictionBetDto getBet() {
		return bet;
	}

	public void setBet(PredictionBetDto bet) {
		this.bet = bet;
	}

	public Boolean getPrediction() {
		return prediction;
	}

	public void setPrediction(Boolean prediction) {
		this.prediction = prediction;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}
}
