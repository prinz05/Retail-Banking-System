package com.mfpe.transaction.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class AccountDto {

	/**
	 * AccountDto Model Class
	 */
	private String accountId;
	
	@Setter
	private double balance;

}
