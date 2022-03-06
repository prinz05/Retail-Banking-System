package com.mfpe.authentication.exceptionhandling;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mfpe.authentication.dto.ErrorMessage;

import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * Global Exception Handler class for Authentication Microservice
	 */

	/**
	 * Exception handler for handling UsernameNotFoundException
	 */
	@ExceptionHandler(UsernameNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorMessage userNotFoundException(UsernameNotFoundException userNotFoundException) {
		return new ErrorMessage(HttpStatus.NOT_FOUND, LocalDateTime.now(), userNotFoundException.getMessage());
	}

	/**
	 * Exception handler for handling MalformedJwtException
	 */
	@ExceptionHandler(MalformedJwtException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ErrorMessage tokenMalformedException() {
		return new ErrorMessage(HttpStatus.UNAUTHORIZED, LocalDateTime.now(), "Not Authorized --> Token is Invalid..");
	}

	/**
	 * Exception handler for handling SignatureException
	 */
	@ExceptionHandler(SignatureException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ErrorMessage tokenSignatureException() {
		return new ErrorMessage(HttpStatus.UNAUTHORIZED, LocalDateTime.now(), "Not Authorized --> Token is Invalid..");
	}

}