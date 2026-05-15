package com.pewniaczekbet.dto;

/**
 * PredictionBetPlaceDto
 */
public class PredictionBetPlaceDto {
	private Long id;
	private Long amount;
	private Boolean prediction;
	private Boolean isFreeBet;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public Boolean getPrediction() {
		return prediction;
	}

	public void setPrediction(Boolean prediction) {
		this.prediction = prediction;
	}

	public Boolean getIsFreeBet() {
		return isFreeBet;
	}

	public void setIsFreeBet(Boolean isFreeBet) {
		this.isFreeBet = isFreeBet;
	}
}
