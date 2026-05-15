package com.pewniaczekbet.model.entities;

import jakarta.persistence.*;
import lombok.Data;

/**
 * UserEntity
 */
@Data
@Entity
@Table(name = "users")
public class UserEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "surname")
	private String surname;

	@Column(name = "email")
	private String email;

	@Column(name = "password")
	private String password;

	@Column(name = "balance")
	private Long balance;

	@Column(name = "free_bet_balance")
	private Long freeBetBalance;

	@Column(name = "wins")
	private Long wins;

	@Column(name = "losses")
	private Long losses;

	@Column(name = "wins_amount")
	private Long winsAmount;

	@Column(name = "losses_amount")
	private Long lossesAmount;

	@Column(name = "is_public")
	private boolean isPublic;

	@Column(name = "account_type_id")
	private Long accountTypeId;

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
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

	public void setPassword(String password) {
		this.password = password;
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

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public String getPassword() {
		return password;
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
}
