package com.mfpe.authentication.dto;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessage {

	/**
	 * ErrorMessage Dto for transferring the information
	 */
	private HttpStatus status;
	private LocalDateTime timestamp;
	private String message;

}