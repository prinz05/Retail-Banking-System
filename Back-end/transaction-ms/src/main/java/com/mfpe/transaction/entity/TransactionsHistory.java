package com.mfpe.transaction.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class TransactionsHistory {

	/**
	 * TransactionsHistory Entity persisted in Repository
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "slno")
	private long id;

	@Setter
	private String transactionId;

	@Setter
	private String transactionType;

	@Setter
	private String sourceAccountId;

	@Setter
	private String destinationAccountId;

	@Setter
	private double amount;

	private static final String MY_TIME_ZONE = "Asia/Kolkata";
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm a", timezone = MY_TIME_ZONE)
	@Setter
	private Date dateOfTransaction;

	@Setter
	private String transactionStatus;

	@Setter
	private double sourceBalance;

	@Setter
	private double destinationBalance;

}
