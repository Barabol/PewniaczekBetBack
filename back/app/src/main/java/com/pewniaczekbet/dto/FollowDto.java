package com.pewniaczekbet.dto;

import com.pewniaczekbet.model.entities.FollowEntity;
import com.pewniaczekbet.model.entities.UserEntity;

/**
 * FollowDto
 */
public class FollowDto {

	private UserDto follower;
	private UserDto followed;

	public FollowEntity toEntity() {
		FollowEntity entity = new FollowEntity();
		entity.setFollower(follower.toEntity());
		entity.setFollowed(followed.toEntity());
		return entity;
	}

	public static FollowDto fromEntity(FollowEntity entity) {
		FollowDto followDto = new FollowDto();
		followDto.setFollowed(UserDto.fromEntity(entity.getFollowed()));
		followDto.setFollower(UserDto.fromEntity(entity.getFollower()));
		return followDto;
	}

	public UserDto getFollower() {
		return follower;
	}

	public void setFollower(UserDto follower) {
		this.follower = follower;
	}

	public UserDto getFollowed() {
		return followed;
	}

	public void setFollowed(UserDto followed) {
		this.followed = followed;
	}

}
