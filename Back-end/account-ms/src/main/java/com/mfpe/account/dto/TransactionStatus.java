package com.mfpe.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class TransactionStatus {

	/**
	 *  TransactionStatus DTO for transferring the information
	 */
	private String message;
	private double sourceBalance;
	private double destinationBalance;
	
}
