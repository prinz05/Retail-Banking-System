package com.mfpe.transaction.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ServiceCharge {

	/**
	 * ServiceCharge Model Class
	 */
	private String accountId;
	private String message;
	private double balance;

}
