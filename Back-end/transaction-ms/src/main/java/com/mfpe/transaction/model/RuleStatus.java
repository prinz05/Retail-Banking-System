package com.mfpe.transaction.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class RuleStatus {

	/**
	 * RuleStatus Model Class
	 */
	@SuppressWarnings("unused")
	private String status;

	@Getter
	private String message;

}
