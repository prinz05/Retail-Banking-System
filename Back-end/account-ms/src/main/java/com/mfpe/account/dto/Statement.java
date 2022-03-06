package com.mfpe.account.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mfpe.account.model.TransactionsHistory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Statement {

	/**
	 * Statement DTO for transferring the information
	 */
	private static final String MY_TIME_ZONE="Asia/Kolkata";
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm a", timezone = MY_TIME_ZONE)
	private Date date;

	private String accountId;

	private double currentBalance;
	
	List<TransactionsHistory> history;

}
