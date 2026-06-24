package com.pewniaczekbet.model.entities;

import java.util.UUID;

import jakarta.persistence.*;

/**
 * OathLoginsEntity
 */
@Entity
@Table(name = "oauth_logins")
public class OathLoginsEntity {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne
	@JoinTable(name = "user_id")
	private UserEntity user;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}
}
