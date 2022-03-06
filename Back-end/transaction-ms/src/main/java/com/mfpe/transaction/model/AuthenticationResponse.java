package com.mfpe.transaction.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class AuthenticationResponse {

	/**
	 * AuthenticationResponse Model Class
	 */
	@Getter
	private String userid;

	@SuppressWarnings("unused")
	private String name;

	@SuppressWarnings("unused")
	private boolean isValid;

}