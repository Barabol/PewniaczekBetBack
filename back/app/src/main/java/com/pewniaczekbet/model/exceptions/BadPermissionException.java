package com.pewniaczekbet.model.exceptions;

/**
 * BadPermissionException
 */
public class BadPermissionException extends RuntimeException {
	private static final String message = "forbidden";

	public BadPermissionException() {
		super(message);
	}
}
