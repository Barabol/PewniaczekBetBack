
package com.pewniaczekbet.model.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pewniaczekbet.model.entities.OathEntity;

public interface OathRepository extends JpaRepository<OathEntity, Long> {
	List<OathEntity> findByUserId(Long userId);

	Optional<OathEntity> findOneByUserIdAndServiceName(Long userId, String serviceName);

	Optional<OathEntity> findByToken(String token);

	void deleteByUserIdAndServiceName(Long userId, String serviceName);

	Optional<OathEntity> findByServiceId(Long serviceId);
}
