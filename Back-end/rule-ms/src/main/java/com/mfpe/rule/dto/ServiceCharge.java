package com.mfpe.rule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ServiceCharge {
	
	/**
	 * ServiceCharge Dto for transferring the information
	 * */
	private String accountId;
	private String message;
	private double balance;

}
