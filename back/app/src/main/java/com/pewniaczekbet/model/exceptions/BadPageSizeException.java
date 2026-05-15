package com.pewniaczekbet.model.exceptions;

import com.pewniaczekbet.other.ApplicationLimitations;

/**
 * BadPageSizeException
 */
public class BadPageSizeException extends RuntimeException {
	private static final String message = "Bad page size was provided, expected <" +
			ApplicationLimitations.MinPageSize + "," + ApplicationLimitations.MaxPageSize + ">";

	public BadPageSizeException() {
		super(message);
	}
}
