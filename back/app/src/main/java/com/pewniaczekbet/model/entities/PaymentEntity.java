package com.pewniaczekbet.model.entities;

import java.time.LocalDateTime;

import jakarta.persistence.*;

/**
 * PaymentEntity
 */
@Entity
@Table(name = "payments")
public class PaymentEntity {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "sid")
	private String sid;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserEntity user;

	@Column(name = "amount")
	private Long amount;

	@Column(name = "description")
	private String description;

	@Column(name = "payment_date")
	private LocalDateTime paymentDate;

	@ManyToOne
	@JoinColumn(name = "status_id")
	private PaymentStatusEntity status;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(LocalDateTime paymentDate) {
		this.paymentDate = paymentDate;
	}

	public PaymentStatusEntity getStatus() {
		return status;
	}

	public void setStatus(PaymentStatusEntity status) {
		this.status = status;
	}
}
