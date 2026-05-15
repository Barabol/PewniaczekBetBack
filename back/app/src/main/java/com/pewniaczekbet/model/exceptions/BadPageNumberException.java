package com.pewniaczekbet.model.exceptions;

import com.pewniaczekbet.other.ApplicationLimitations;

/**
 * BadPageNumberException
 */
public class BadPageNumberException extends RuntimeException {
	private static final String message = "bad page number was provided, expected n > "
			+ ApplicationLimitations.MinPageNumber;

	public BadPageNumberException() {
		super(message);
	}
}
