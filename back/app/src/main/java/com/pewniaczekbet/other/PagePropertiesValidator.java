package com.pewniaczekbet.other;

import com.pewniaczekbet.model.exceptions.BadPageNumberException;
import com.pewniaczekbet.model.exceptions.BadPageSizeException;

/**
 * PagePropertiesValidator
 */
public abstract class PagePropertiesValidator {
	public static void validate(int page, int pageSize) {
		if (page < ApplicationLimitations.MinPageNumber)
			throw new BadPageNumberException();
		if (pageSize < ApplicationLimitations.MinPageSize || pageSize > ApplicationLimitations.MaxPageSize)
			throw new BadPageSizeException();
	}
}
