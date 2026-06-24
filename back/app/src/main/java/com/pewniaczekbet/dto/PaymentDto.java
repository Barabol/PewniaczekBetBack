package com.pewniaczekbet.dto;

import java.time.LocalDateTime;

import com.pewniaczekbet.model.entities.PaymentEntity;

/**
 * PaymentDto
 */
public class PaymentDto {
	private String sid;
	private Long amount;
	private String description;
	private LocalDateTime paymentDate;
	private String status;

	public static PaymentDto fromEntity(PaymentEntity entity) {
		PaymentDto dto = new PaymentDto();
		dto.setSid(entity.getSid());
		dto.setAmount(entity.getAmount());
		dto.setStatus(entity.getStatus().getName());
		dto.setPaymentDate(entity.getPaymentDate());
		dto.setDescription(entity.getDescription());
		return dto;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
