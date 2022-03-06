package com.mfpe.rule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RuleStatus {

	/**
	 * RuleStatus Dto for transferring the information
	 */
	private String status;
	private String message;

}
