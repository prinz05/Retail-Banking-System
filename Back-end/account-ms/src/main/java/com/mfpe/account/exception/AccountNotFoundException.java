package com.mfpe.account.exception;

public class AccountNotFoundException extends RuntimeException {

	/**
	 * AccountNotFoundException Exception Class when the user requests for account
	 * which doesn't exist
	 */
	private static final long serialVersionUID = -6158258448697521823L;

	public AccountNotFoundException() {
		super();
	}

}
