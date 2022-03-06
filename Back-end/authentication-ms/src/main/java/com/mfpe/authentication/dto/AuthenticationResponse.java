package com.mfpe.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {

	/**
	 * AuthenticationResponse Dto for transferring the information
	 */
	private String userid;
	private String name;
	private boolean isValid;

}