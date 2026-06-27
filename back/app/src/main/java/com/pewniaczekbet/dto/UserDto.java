package com.pewniaczekbet.dto;

import java.util.List;

import com.pewniaczekbet.model.entities.UserEntity;

import lombok.Data;

/**
 * UserDto
 */
@Data
public class UserDto {
	private Long id;
	private String name;
	private String surname;
	private Long balance;
	private Long freeBetBalance;
	private Long wins;
	private Long losses;
	private Long winsAmount;
	private Long lossesAmount;
	private boolean isPublic;
	private Long accountTypeId;

	public UserEntity toEntity() {
		UserEntity entity = new UserEntity();
		entity.setId(null);
		entity.setName(this.name);
		entity.setSurname(this.surname);
		entity.setPassword(null);
		entity.setBalance(this.balance);
		entity.setFreeBetBalance(this.freeBetBalance);
		entity.setWins(this.wins);
		entity.setLosses(this.losses);
		entity.setWinsAmount(this.winsAmount);
		entity.setLossesAmount(this.lossesAmount);
		entity.setPublic(this.isPublic);
		entity.setAccountTypeId(this.accountTypeId);
		return entity;
	}

	public static UserDto fromEntity(UserEntity entity) {
		UserDto dto = new UserDto();
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setSurname(entity.getSurname());
		dto.setBalance(entity.getBalance());
		dto.setFreeBetBalance(entity.getFreeBetBalance());
		dto.setWins(entity.getWins());
		dto.setLosses(entity.getLosses());
		dto.setWinsAmount(entity.getWinsAmount());
		dto.setLossesAmount(entity.getLossesAmount());
		dto.setPublic(entity.isPublic());
		dto.setAccountTypeId(entity.getAccountTypeId());
		return dto;
	}

	/*
	public static List<UserDto> fromEntity(List<UserEntity> entities) {
		return entities.stream().map(UserDto::fromEntity).toList();
	}
	*/

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public Long getBalance() {
		return balance;
	}

	public Long getFreeBetBalance() {
		return freeBetBalance;
	}

	public Long getWins() {
		return wins;
	}

	public Long getLosses() {
		return losses;
	}

	public Long getWinsAmount() {
		return winsAmount;
	}

	public Long getLossesAmount() {
		return lossesAmount;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public Long getAccountTypeId() {
		return accountTypeId;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public void setBalance(Long balance) {
		this.balance = balance;
	}

	public void setFreeBetBalance(Long freeBetBalance) {
		this.freeBetBalance = freeBetBalance;
	}

	public void setWins(Long wins) {
		this.wins = wins;
	}

	public void setLosses(Long losses) {
		this.losses = losses;
	}

	public void setWinsAmount(Long winsAmount) {
		this.winsAmount = winsAmount;
	}

	public void setLossesAmount(Long lossesAmount) {
		this.lossesAmount = lossesAmount;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public void setAccountTypeId(Long accountTypeId) {
		this.accountTypeId = accountTypeId;
	}

}
