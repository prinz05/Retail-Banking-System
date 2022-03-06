package com.mfpe.rule.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class Account {

	/**
	 * Account Model Class
	 */
	@Getter
	private String accountId;

	@SuppressWarnings("unused")
	private String customerId;

	@Getter
	private double balance;

	@SuppressWarnings("unused")
	private AccountType accountType;

}