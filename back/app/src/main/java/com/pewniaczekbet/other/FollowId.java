package com.pewniaczekbet.other;

import java.io.Serializable;

public class FollowId implements Serializable {
	private Long follower;
	private Long followed;

	public Long getFollower() {
		return follower;
	}

	public void setFollower(Long follower) {
		this.follower = follower;
	}

	public Long getFollowed() {
		return followed;
	}

	public void setFollowed(Long followed) {
		this.followed = followed;
	}
}
