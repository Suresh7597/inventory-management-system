package com.inventory.management.auth;

public class UsernameAlreadyExistsException extends RuntimeException {

	public UsernameAlreadyExistsException(String username) {
		super("Username " + username + " is already registered");
	}

}
