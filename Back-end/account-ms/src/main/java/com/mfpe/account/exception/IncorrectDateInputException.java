package com.mfpe.account.exception;

public class IncorrectDateInputException extends RuntimeException {

	/**
	 * DateInputException Exception Class which will be raised when user enters
	 * dates for fromDate and toDate in reverse order
	 */
	private static final long serialVersionUID = -2260479592034067215L;

	public IncorrectDateInputException() {
		super();
	}

}
