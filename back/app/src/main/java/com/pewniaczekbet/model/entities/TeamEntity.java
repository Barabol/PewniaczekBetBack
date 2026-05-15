package com.pewniaczekbet.model.entities;

import jakarta.persistence.*;

/**
 * SportEntity
 */
@Entity
@Table(name = "teams")
public class TeamEntity {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "name")
	private String name;

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
}
