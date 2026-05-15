package com.pewniaczekbet.model.entities;

import com.pewniaczekbet.other.FollowId;

import jakarta.persistence.*;
import lombok.Data;

/**
 * FollowEntity
 */
@Data
@Table(name = "followers")
@Entity
@IdClass(FollowId.class)
public class FollowEntity {

	@Id
	@ManyToOne
	@JoinColumn(name = "follower_id")
	private UserEntity follower;

	@Id
	@ManyToOne
	@JoinColumn(name = "followed_id")
	private UserEntity followed;

	public void setFollower(UserEntity follower) {
		this.follower = follower;
	}

	public void setFollowed(UserEntity followed) {
		this.followed = followed;
	}

	public UserEntity getFollower() {
		return follower;
	}

	public UserEntity getFollowed() {
		return followed;
	}

}
