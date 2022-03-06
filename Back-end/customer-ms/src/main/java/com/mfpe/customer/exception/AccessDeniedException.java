package com.mfpe.customer.exception;

public class AccessDeniedException extends RuntimeException {

	/**
	 * AccessDeniedException Exception Class which will be raised when the user
	 * tries to request for unauthorized resource
	 */
	private static final long serialVersionUID = 3614881436315163492L;

	public AccessDeniedException() {
		super();
	}

}