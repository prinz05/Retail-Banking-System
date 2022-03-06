package com.mfpe.account.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AccountExceptionDetails {

	/**
	 * AccountExceptionDetails DTO for transferring the error information
	 */
	private LocalDateTime timestamp;

	private int status;

	private String message;
}
