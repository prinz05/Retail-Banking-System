package com.mfpe.account.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransactionsHistory {

	/**
	 * TransactionsHistory Model class
	 */
	private long id;

	private String transactionId;

	private String transactionType;

	private String sourceAccountId;

	private String destinationAccountId;

	private double amount;

	private static final String MY_TIME_ZONE = "Asia/Kolkata";

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm a", timezone = MY_TIME_ZONE)
	private Date dateOfTransaction;

	private String transactionStatus;

	private double sourceBalance;

	private double destinationBalance;

}
