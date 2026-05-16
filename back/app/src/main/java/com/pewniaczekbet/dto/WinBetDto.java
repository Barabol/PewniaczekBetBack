package com.pewniaczekbet.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.pewniaczekbet.model.entities.WinBetEntity;

import lombok.Data;

/**
 * WinBetDto
 */
@Data
public class WinBetDto {
	private Long id;
	private String name;
	private Double currentMultiplier;
	private LocalDateTime stopDate;
	private GameDto game;

	public static WinBetDto fromEntity(WinBetEntity entity) {
		WinBetDto dto = new WinBetDto();
		dto.setId(entity.getId());
		dto.setStopDate(entity.getStopDate());
		dto.setName(entity.getName());
		dto.setCurrentMultiplier(entity.getCurrentMultiplier());
		dto.setGame(GameDto.fromEntity(entity.getGame()));
		return dto;
	}

	public static List<WinBetDto> fromEntity(List<WinBetEntity> entities) {
		return entities.stream().map(WinBetDto::fromEntity).toList();
	}

	public LocalDateTime getStopDate() {
		return stopDate;
	}

	public void setStopDate(LocalDateTime stopDate) {
		this.stopDate = stopDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getCurrentMultiplier() {
		return currentMultiplier;
	}

	public void setCurrentMultiplier(Double currentMultiplier) {
		this.currentMultiplier = currentMultiplier;
	}

	public GameDto getGame() {
		return game;
	}

	public void setGame(GameDto game) {
		this.game = game;
	}
}
