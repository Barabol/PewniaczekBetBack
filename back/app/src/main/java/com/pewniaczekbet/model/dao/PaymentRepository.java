package com.pewniaczekbet.model.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pewniaczekbet.model.entities.PaymentEntity;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
	Optional<PaymentEntity> findBySid(String sid);

	Page<PaymentEntity> findByUserId(String sid, Pageable pageable);

	List<PaymentEntity> findByStatusName(String name);
}
