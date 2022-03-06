package com.mfpe.customer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AppUser {

	/**
	 * AppUser Model Class
	 */
	private String userid;

	private String username;

	private String password;

	private String authToken;

	private String role;
}