package com.inventory.management.common;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.inventory.management.auth.EmailAlreadyExistsException;
import com.inventory.management.auth.UsernameAlreadyExistsException;
import com.inventory.management.inventory.InventoryItemNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(InventoryItemNotFoundException.class)
	public ResponseEntity<ApiError> handleNotFound(InventoryItemNotFoundException exception, HttpServletRequest request) {
		return buildError(HttpStatus.NOT_FOUND, exception.getMessage(), request.getRequestURI(), Map.of());
	}

	@ExceptionHandler(UsernameAlreadyExistsException.class)
	public ResponseEntity<ApiError> handleUsernameAlreadyExists(
		UsernameAlreadyExistsException exception,
		HttpServletRequest request
	) {
		return buildError(HttpStatus.CONFLICT, exception.getMessage(), request.getRequestURI(), Map.of());
	}

	@ExceptionHandler(EmailAlreadyExistsException.class)
	public ResponseEntity<ApiError> handleEmailAlreadyExists(
		EmailAlreadyExistsException exception,
		HttpServletRequest request
	) {
		return buildError(HttpStatus.CONFLICT, exception.getMessage(), request.getRequestURI(), Map.of());
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException exception, HttpServletRequest request) {
		return buildError(HttpStatus.UNAUTHORIZED, "Invalid username or password", request.getRequestURI(), Map.of());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
		Map<String, String> fieldErrors = new LinkedHashMap<>();
		for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
			fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
		}
		return buildError(HttpStatus.BAD_REQUEST, "Validation failed", request.getRequestURI(), fieldErrors);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleUnexpected(Exception exception, HttpServletRequest request) {
		return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request.getRequestURI(), Map.of());
	}

	private ResponseEntity<ApiError> buildError(
		HttpStatus status,
		String message,
		String path,
		Map<String, String> fieldErrors
	) {
		ApiError apiError = new ApiError(
			LocalDateTime.now(),
			status.value(),
			status.getReasonPhrase(),
			message,
			path,
			fieldErrors
		);
		return ResponseEntity.status(status).body(apiError);
	}

}
