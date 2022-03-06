package com.mfpe.customer.exception;

public class CustomerAlreadyExistException extends RuntimeException {

	/**
	 * CustomerAlreadyExistException Exception Class which will be raised when the
	 * employee tries to create the already existing customer again
	 */
	private static final long serialVersionUID = -9077866631588338462L;

	public CustomerAlreadyExistException() {
		super();
	}

}
