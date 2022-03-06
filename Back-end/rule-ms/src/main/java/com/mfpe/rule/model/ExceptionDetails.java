package com.mfpe.rule.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ExceptionDetails {
	
	/**
	 *  AccountExceptionDetails DTO for transferring the error information
	 */
	private LocalDateTime timestamp;
	private int status;
	private String message;
}
