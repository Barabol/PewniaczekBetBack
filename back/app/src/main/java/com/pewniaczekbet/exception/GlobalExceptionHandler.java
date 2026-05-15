package com.pewniaczekbet.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;

import com.pewniaczekbet.model.exceptions.*;

/**
 * GlobalExceptionHandler
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<String> handleBadRequest(BadRequestException e) {
		return ResponseEntity.badRequest().body(e.getMessage());
	}

	@ExceptionHandler(InternalServerErrorException.class)
	public ResponseEntity<String> handleInternalServerError(InternalServerErrorException e) {
		return ResponseEntity.internalServerError().body(e.getMessage());
	}

	@ExceptionHandler(BadPageNumberException.class)
	public ResponseEntity<String> handleBadPageNumber(BadPageNumberException e) {
		return ResponseEntity.badRequest().body(e.getMessage());
	}

	@ExceptionHandler(BadPageSizeException.class)
	public ResponseEntity<String> handleBadPageSize(BadPageSizeException e) {
		return ResponseEntity.badRequest().body(e.getMessage());
	}

	@ExceptionHandler(NotLoggedInException.class)
	public ResponseEntity<String> handleUnouthorized(NotLoggedInException e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
	}

	@ExceptionHandler(BadPermissionException.class)
	public ResponseEntity<String> handleForbiden(BadPermissionException e) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<String> handleNotFound(NotFoundException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	}
}
