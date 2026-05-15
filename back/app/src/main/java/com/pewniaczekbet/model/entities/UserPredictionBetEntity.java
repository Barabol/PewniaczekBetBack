package com.pewniaczekbet.model.entities;

import jakarta.persistence.*;

/**
 * UserPredictionBetEntity
 */
@Entity
@Table(name = "user_predictions")
public class UserPredictionBetEntity {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserEntity user;

	@ManyToOne
	@JoinColumn(name = "prediction_id")
	private PredictionBetEntity prediction;

	@Column(name = "predicted")
	private Boolean predicted;

	@Column(name = "amount")
	private Long amount;

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

	public PredictionBetEntity getPrediction() {
		return prediction;
	}

	public void setPrediction(PredictionBetEntity prediction) {
		this.prediction = prediction;
	}

	public Boolean getPredicted() {
		return predicted;
	}

	public void setPredicted(Boolean predicted) {
		this.predicted = predicted;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}
}
