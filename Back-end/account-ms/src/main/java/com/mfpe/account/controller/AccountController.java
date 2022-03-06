package com.mfpe.account.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.mfpe.account.dto.AccountCreationStatus;
import com.mfpe.account.dto.AccountDto;
import com.mfpe.account.dto.Statement;
import com.mfpe.account.dto.TransactionStatus;
import com.mfpe.account.exception.AccessDeniedException;
import com.mfpe.account.exception.TokenNotFoundException;
import com.mfpe.account.feign.AuthenticationFeign;
import com.mfpe.account.model.AuthenticationResponse;
import com.mfpe.account.service.AccountService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@CrossOrigin
public class AccountController {

	/**
	 * Controller Layer class for Account Microservice
	 */

	// autowiring the Account Service
	@Autowired
	private AccountService accountServiceImpl;

	// autowiring the Authentication feign client
	@Autowired
	private AuthenticationFeign authenticationFeign;

	/**
	 * @param customerId
	 * @return ResponseEntiy of AccountCreationStatus which contains a success
	 *         message and created account IDs
	 */
	@PostMapping("/createAccount/{customerId}")
	public ResponseEntity<AccountCreationStatus> createAccount(@PathVariable("customerId") String customerId) {

		log.info("requesting account service to create accounts");
		return new ResponseEntity<>(this.accountServiceImpl.createAccount(customerId), HttpStatus.CREATED);

	}

	/**
	 * @param customerId
	 * @return List of ResponseEntity of AccountDto of the customer which contains
	 *         accountId and respective account's balance
	 */
	@GetMapping("/getCustomerAccounts/{customerId}")
	public ResponseEntity<List<AccountDto>> getCustomerAccounts(@PathVariable("customerId") String customerId) {

		log.info("requesting account service to get all associated accounts' details");
		return new ResponseEntity<>(this.accountServiceImpl.getCustomerAccounts(customerId), HttpStatus.OK);

	}

	/**
	 * @param accountId
	 * @return ResponseEntity of AccountDto which contains accountId and respective
	 *         account's balance
	 * @throws TokenNotFoundException when request header doesn't contains JWT token
	 * @throws AccessDeniedException  when JWT token attached with the request is
	 *                                invalid or if the employee tries to access
	 *                                this resource
	 */
	@GetMapping("/getAccount/{accountId}")
	public ResponseEntity<AccountDto> getAccount(@RequestHeader(value = "Authorization", required = false) String token,
			@PathVariable("accountId") String accountId) {

		// if token is null, throws TokenNotFoundException
		if (token == null) {
			log.error("JWT required for validating the access to this resource");
			throw new TokenNotFoundException();
		}

		// requesting authentication feign to check for token validity and stores the
		// response
		AuthenticationResponse validity = authenticationFeign.getValidity(token);

		// checks whether the requested user's role is customer or not, if yes
		if (authenticationFeign.getRole(validity.getUserid()).equals("CUSTOMER")) {

			log.info("requesting account service to get account details");
			return new ResponseEntity<>(this.accountServiceImpl.getAccount(accountId, validity.getUserid()),
					HttpStatus.OK);
			// if no, throws AccessDeniedException
		} else {

			log.error("Only customer can access this resource");
			throw new AccessDeniedException();
		}
	}

	/**
	 * @param accountId
	 * @param fromDate
	 * @param toDate
	 * @return ResponseEntity of Statement which contains current date, account ID,
	 *         current balance and the transaction history between the given date
	 *         range
	 * @throws TokenNotFoundException when request header doesn't contains JWT token
	 * @throws AccessDeniedException  when JWT token attached with the request is
	 *                                invalid or if the employee tries to access
	 *                                this resource
	 */
	@GetMapping("/getAccountStatement/{accountId}/{fromDate}/{toDate}")
	public ResponseEntity<Statement> getAccountStatementBetweenDates(
			@RequestHeader(value = "Authorization", required = false) String token,
			@PathVariable("accountId") String accountId,
			@PathVariable("fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
			@PathVariable("toDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate) {

		// if token is null, throws TokenNotFoundException
		if (token == null) {
			log.error("JWT required for validating the access to this resource");
			throw new TokenNotFoundException();
		}

		// requesting authentication feign to check for token validity and stores the
		// response
		AuthenticationResponse validity = authenticationFeign.getValidity(token);

		// checks whether the requested user's role is customer or not, if yes
		if (authenticationFeign.getRole(validity.getUserid()).equals("CUSTOMER")) {

			log.info("requesting account service to get account statement");
			return new ResponseEntity<>(this.accountServiceImpl.getAccountStatement(accountId, fromDate, toDate, token),
					HttpStatus.OK);
			// if no, throws AccessDeniedException
		} else {
			log.error("Only customer can access this resource");
			throw new AccessDeniedException();
		}

	}

	/**
	 * @param accountId
	 * @return ResponseEntity of Statement which contains current date, account ID,
	 *         current balance and the transaction history for the current month
	 * @throws TokenNotFoundException when request header doesn't contains JWT token
	 * @throws AccessDeniedException  when JWT token attached with the request is
	 *                                invalid or if the employee tries to access
	 *                                this resource
	 */
	@GetMapping("/getAccountStatement/{accountId}")
	public ResponseEntity<Statement> getAccountStatement(
			@RequestHeader(value = "Authorization", required = false) String token,
			@PathVariable("accountId") String accountId) {

		// if token is null, throws TokenNotFoundException
		if (token == null) {
			log.error("JWT required for validating the access to this resource");
			throw new TokenNotFoundException();
		}

		// requesting authentication feign to check for token validity and stores the
		// response
		AuthenticationResponse validity = authenticationFeign.getValidity(token);

		// checks whether the requested user's role is customer or not, if yes
		if (authenticationFeign.getRole(validity.getUserid()).equals("CUSTOMER")) {

			log.info("requesting account service to get account statement");
			return new ResponseEntity<>(this.accountServiceImpl.getAccountStatement(accountId, token), HttpStatus.OK);

			// if no, throws AccessDeniedException
		} else {
			log.error("Only customer can access this resource");
			throw new AccessDeniedException();
		}

	}

	/**
	 * @param accountId
	 * @param amount
	 * @return ResponseEntity of TransactionStatus which contains a
	 *         success/unsuccess message, source balance & destination balance
	 */
	@PostMapping("/deposit/{accountId}/{amount}")
	public ResponseEntity<TransactionStatus> deposit(@PathVariable("accountId") String accountId,
			@PathVariable("amount") double amount) {

		log.info("requesting account service to deposit amount");
		return new ResponseEntity<>(this.accountServiceImpl.deposit(accountId, amount), HttpStatus.OK);

	}

	/**
	 * @param accountId
	 * @param amount
	 * @return ResponseEntity of TransactionStatus which contains a
	 *         success/unsuccess message, source balance & destination balance
	 * @throws TokenNotFoundException when request header doesn't contains JWT token
	 */
	@PostMapping("/withdraw/{accountId}/{amount}")
	public ResponseEntity<TransactionStatus> withdraw(
			@RequestHeader(value = "Authorization", required = false) String token,
			@PathVariable("accountId") String accountId, @PathVariable("amount") double amount) {

		// if token is null, throws TokenNotFoundException
		if (token == null) {
			log.error("JWT required for validating the access to this resource");
			throw new TokenNotFoundException();
		}

		// requesting authentication feign to check for token validity and stores the
		// response
		AuthenticationResponse validity = authenticationFeign.getValidity(token);

		// fetching the user Id from the authentication feign's response
		String customerId = validity.getUserid();

		log.info("requesting account service to withdraw amount");
		return new ResponseEntity<>(this.accountServiceImpl.withdraw(accountId, amount, customerId), HttpStatus.OK);

	}

}