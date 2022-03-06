package com.mfpe.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AccountCreationStatus {

	/**
	 *  AccountCreationStatus DTO for transferring the information
	 */
	private String savingsAccountId;
	private String currentAccountId;
	private String message;
	
}
