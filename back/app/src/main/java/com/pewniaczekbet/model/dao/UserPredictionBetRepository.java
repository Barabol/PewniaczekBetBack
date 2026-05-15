package com.pewniaczekbet.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pewniaczekbet.model.entities.UserPredictionBetEntity;

public interface UserPredictionBetRepository extends JpaRepository<UserPredictionBetEntity, Long> {
}
