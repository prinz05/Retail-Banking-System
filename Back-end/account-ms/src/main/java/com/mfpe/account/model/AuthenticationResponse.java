package com.mfpe.account.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class AuthenticationResponse {

	/**
	 * AuthenticationResponse Dto transferring the information
	 */
	@Getter
	private String userid;

	@SuppressWarnings("unused")
	private String name;

	@SuppressWarnings("unused")
	private boolean isValid;

}