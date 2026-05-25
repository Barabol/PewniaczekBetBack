package com.pewniaczekbet.model.dao;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pewniaczekbet.model.entities.PaymentStatusEntity;

public interface PaymentStatusRepository extends JpaRepository<PaymentStatusEntity, Long> {
	Optional<PaymentStatusEntity> findByName(String name);
}
