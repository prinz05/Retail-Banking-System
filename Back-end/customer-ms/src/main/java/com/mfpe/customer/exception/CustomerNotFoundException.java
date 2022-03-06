package com.mfpe.customer.exception;

public class CustomerNotFoundException extends RuntimeException {

	/**
	 * CustomerNotFoundException Exception Class which will be raised when the user
	 * requests for non-existing customer
	 */
	private static final long serialVersionUID = -8445340862384469098L;

	public CustomerNotFoundException() {
		super();
	}

}
