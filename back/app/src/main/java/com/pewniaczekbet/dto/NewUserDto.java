package com.pewniaczekbet.dto;

import org.mindrot.jbcrypt.BCrypt;

import com.pewniaczekbet.model.entities.UserEntity;

/**
 * NewUserDto
 */
public class NewUserDto {

	private String name;
	private String surname;
	private String email;
	private String password;

	public UserEntity toEntity() {
		UserEntity entity = new UserEntity();
		entity.setName(name);
		entity.setSurname(surname);
		entity.setEmail(email);
		entity.setPassword(password);
		return entity;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public String getEmail() {
		return email;
	}

}
