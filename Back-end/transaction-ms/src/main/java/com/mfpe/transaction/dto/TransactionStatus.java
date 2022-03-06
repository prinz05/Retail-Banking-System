package com.mfpe.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class TransactionStatus {

	/**
	 * TransactionStatus DTO for transferring the information
	 */
	private String message;
	private double sourceBalance;
	private double destinationBalance;

}
