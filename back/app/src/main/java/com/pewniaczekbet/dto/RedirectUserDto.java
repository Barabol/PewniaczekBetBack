package com.pewniaczekbet.dto;

import org.springframework.web.servlet.view.RedirectView;

/**
 * RedirectUserDto
 */
public class RedirectUserDto {
	private RedirectView redirect;
	private UserDto user;

	public RedirectView getRedirect() {
		return redirect;
	}

	public void setRedirect(RedirectView redirect) {
		this.redirect = redirect;
	}

	public UserDto getUser() {
		return user;
	}

	public void setUser(UserDto user) {
		this.user = user;
	}
}
