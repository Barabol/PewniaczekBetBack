package com.pewniaczekbet.dto;

import java.time.LocalDateTime;

import com.pewniaczekbet.model.entities.LogEntity;

/**
 * LogDto
 */
public class LogDto {
	private UserDto user;
	private String log;
	private LocalDateTime time;

	public static LogDto fromEntity(LogEntity entity) {
		LogDto dto = new LogDto();
		dto.setUser(UserDto.fromEntity(entity.getUser()));
		dto.setLog(entity.getValue());
		dto.setTime(entity.getTime());
		return dto;
	}

	public UserDto getUser() {
		return user;
	}

	public void setUser(UserDto user) {
		this.user = user;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}
}
