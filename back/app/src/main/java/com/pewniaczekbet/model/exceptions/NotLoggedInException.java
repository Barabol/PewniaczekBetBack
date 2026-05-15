package com.pewniaczekbet.model.exceptions;

/**
 * NotLoggedInException
 */
public class NotLoggedInException extends RuntimeException {
	private static final String message = "you must be logged in to use this function";

	public NotLoggedInException() {
		super(message);
	}
}
