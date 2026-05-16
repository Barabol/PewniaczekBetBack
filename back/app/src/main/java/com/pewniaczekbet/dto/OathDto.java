package com.pewniaczekbet.dto;

import com.pewniaczekbet.model.entities.OathEntity;

/**
 * OathDto
 */
public class OathDto {
	private String service;
	private String login;
	private String avatarUrl;
	private String url;

	public static OathDto fromEntity(OathEntity entity) {
		OathDto dto = new OathDto();
		dto.setUrl(entity.getUrl());
		dto.setAvatarUrl(entity.getAvatarUrl());
		dto.setLogin(entity.getLogin());
		dto.setService(entity.getService().getName());
		return dto;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
}
