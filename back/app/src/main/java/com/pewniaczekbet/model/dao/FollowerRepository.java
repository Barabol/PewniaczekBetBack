
package com.pewniaczekbet.model.dao;

import com.pewniaczekbet.model.entities.FollowEntity;
import com.pewniaczekbet.other.FollowId;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowerRepository extends JpaRepository<FollowEntity, FollowId> {
	Page<FollowEntity> findByFollowerId(Long follower, Pageable pageable);

	Page<FollowEntity> findByFollowedId(Long followed, Pageable pageable);

	Optional<FollowEntity> findByFollowedIdAndFollowerId(Long followed, Long follower);
}
