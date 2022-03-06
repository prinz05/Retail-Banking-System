package com.mfpe.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class AccountDto {

	/**
	 * Account DTO for transferring the information
	 */
	private String accountId;
	private double balance;

}
