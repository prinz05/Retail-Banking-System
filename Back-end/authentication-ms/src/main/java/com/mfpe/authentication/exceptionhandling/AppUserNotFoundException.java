package com.mfpe.authentication.exceptionhandling;

public class AppUserNotFoundException extends Exception {

	/**
	 * AppUserNotFoundException Exception Class which will be raised when provided
	 * credentials doesn't exist
	 */
	private static final long serialVersionUID = 1L;

	public AppUserNotFoundException() {
		super();
	}

	public AppUserNotFoundException(final String message) {
		super(message);
	}
}