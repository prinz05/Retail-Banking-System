package com.mfpe.customer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {

	/**
	 * AuthenticationResponse Model Class
	 */
	@Getter
	@Setter
	private String userid;
	
	@SuppressWarnings("unused")
	private String name;
	
	@Getter
	@Setter
	private boolean isValid;

}