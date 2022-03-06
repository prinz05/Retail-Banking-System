package com.mfpe.rule.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AccountDto {

	/**
	 * AccountDto class for transferring the information
	 */
	private String accountId;
	
	private double balance;

}
