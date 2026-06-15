package com.inventory.management.auth;

public class EmailAlreadyExistsException extends RuntimeException {

	public EmailAlreadyExistsException(String email) {
		super("Email " + email + " is already registered");
	}

}
