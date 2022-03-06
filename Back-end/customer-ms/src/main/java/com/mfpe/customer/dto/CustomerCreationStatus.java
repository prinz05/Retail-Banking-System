package com.mfpe.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class CustomerCreationStatus {

	/**
	 * CustomerCreationStatus Dto for transferring the information
	 */
	@Getter
	private String message;

	@Getter
	private String customerId;

}
