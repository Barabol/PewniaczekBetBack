package com.pewniaczekbet.model.entities;

import jakarta.persistence.*;

/**
 * OathEntity
 */
@Entity
@Table(name = "oath")
public class OathEntity {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserEntity user;

	@Column(name = "token")
	private String token;

	@ManyToOne
	@JoinColumn(name = "service_id")
	private OathServiceEntity service;

	@Column(name = "login")
	private String login;

	@Column(name = "email")
	private String email;

	@Column(name = "avatar_url")
	private String avatarUrl;

	@Column(name = "url")
	private String url;

	@Column(name = "service_user_id")
	private Long serviceId;

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

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

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public OathServiceEntity getService() {
		return service;
	}

	public void setService(OathServiceEntity service) {
		this.service = service;
	}
}
