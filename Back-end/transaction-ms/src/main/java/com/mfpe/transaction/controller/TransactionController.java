package com.mfpe.transaction.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.mfpe.transaction.dto.TransactionStatus;
import com.mfpe.transaction.entity.TransactionsHistory;
import com.mfpe.transaction.exception.AccessDeniedException;
import com.mfpe.transaction.exception.TokenNotFoundException;
import com.mfpe.transaction.feign.AuthenticationFeign;
import com.mfpe.transaction.model.AuthenticationResponse;
import com.mfpe.transaction.model.ServiceCharge;
import com.mfpe.transaction.service.TransactionService;

import lombok.extern.slf4j.Slf4j;

@RestController
@EnableFeignClients
@Slf4j
@CrossOrigin(origins = "*")
public class TransactionController {

	/**
	 * Controller Layer Class for Transaction Microservice
	 */

	// autowiring the transaction service
	@Autowired
	private TransactionService transactionService;

	// autowiring the authentication feign client
	@Autowired
	private AuthenticationFeign authenticationClient;

	/**
	 * @param token
	 * @param accountId
	 * @param amount
	 * @return ResponseEntity of TransactionStatus that contains a message,
	 *         sourceBalance and destinationBalance
	 * @throws TokenNotFoundException when the request header doesn't contains JWT
	 *                                token
	 * @throws AccessDeniedException  when the unauthorized customer or employee
	 *                                tries to access this resource
	 */
	@PostMapping("/deposit/{accountId}/{amount}")
	public ResponseEntity<TransactionStatus> deposit(
			@RequestHeader(value = "Authorization", required = false) String token,
			@PathVariable("accountId") String accountId, @PathVariable("amount") double amount) {

		// if token is null, throws TokenNotFoundException
		if (token == null) {
			log.error("JWT required for validating the access to this resource");
			throw new TokenNotFoundException();
		}

		// requesting the authentication client to check for the validity of the token
		// and stores the response
		AuthenticationResponse validity = authenticationClient.getValidity(token);

		// if the requested user's role is customer
		if (authenticationClient.getRole(validity.getUserid()).equals("CUSTOMER")) {

			log.info("requesting Transaction service to deposit the money");
			return new ResponseEntity<>(this.transactionService.deposit(accountId, amount), HttpStatus.OK);

			// if not, throws AccessDeniedException
		} else {
			log.error("Only customer can access this resource");
			throw new AccessDeniedException();
		}
	}

	/**
	 * @param token
	 * @param accountId
	 * @param amount
	 * @return ResponseEntity of TransactionStatus that contains a message,
	 *         sourceBalance and destinationBalance
	 * @throws TokenNotFoundException when the request header doesn't contains JWT
	 *                                token
	 * @throws AccessDeniedException  when the unauthorized customer or employee
	 *                                tries to access this resource
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

		// requesting the authentication client to check for the validity of the token
		// and stores the response
		AuthenticationResponse validity = authenticationClient.getValidity(token);

		// if the requested user's role is customer
		if (authenticationClient.getRole(validity.getUserid()).equals("CUSTOMER")) {

			log.info("requesting Transaction service to withdraw the money");
			return new ResponseEntity<>(this.transactionService.withdraw(accountId, amount, token), HttpStatus.OK);

			// if not, throws AccessDeniedException
		} else {
			log.error("Only customer can access this resource");
			throw new AccessDeniedException();
		}
	}

	/**
	 * @param token
	 * @param sourceAccountId
	 * @param destinationAccountId
	 * @param amount
	 * @return ResponseEntity of TransactionStatus that contains a message,
	 *         sourceBalance and destinationBalance
	 * @throws TokenNotFoundException when the request header doesn't contains JWT
	 *                                token
	 * @throws AccessDeniedException  when the unauthorized customer or employee
	 *                                tries to access this resource
	 */
	@PostMapping("/transfer/{sourceAccountId}/{destinationAccountId}/{amount}")
	public ResponseEntity<TransactionStatus> transfer(
			@RequestHeader(value = "Authorization", required = false) String token,
			@PathVariable("sourceAccountId") String sourceAccountId,
			@PathVariable("destinationAccountId") String destinationAccountId, @PathVariable("amount") double amount) {

		// if token is null, throws TokenNotFoundException
		if (token == null) {
			log.error("JWT required for validating the access to this resource");
			throw new TokenNotFoundException();

		}

		// requesting the authentication client to check for the validity of the token
		// and stores the response
		AuthenticationResponse validity = authenticationClient.getValidity(token);

		// if the requested user's role is customer
		if (authenticationClient.getRole(validity.getUserid()).equals("CUSTOMER")) {

			log.info("requesting Transaction service to transfer the money");
			return new ResponseEntity<>(
					this.transactionService.transfer(sourceAccountId, destinationAccountId, amount, token),
					HttpStatus.OK);

			// if not, throws AccessDeniedException
		} else {
			log.error("Only customer can access this resource");
			throw new AccessDeniedException();
		}

	}

	/**
	 * @param token
	 * @param accountId
	 * @return ResponseEntity of List of TransactionHistory that contains all
	 *         details of transactions
	 * @throws TokenNotFoundException when the request header doesn't contains JWT
	 *                                token
	 * @throws AccessDeniedException  when the unauthorized customer or employee
	 *                                tries to access this resource
	 */
	@GetMapping("/getTransactions/{accountId}")
	public ResponseEntity<List<TransactionsHistory>> getTransactions(
			@RequestHeader(value = "Authorization", required = false) String token,
			@PathVariable("accountId") String accountId) {

		// if token is null, throws TokenNotFoundException
		if (token == null) {
			log.error("JWT required for validating the access to this resource");
			throw new TokenNotFoundException();

		}

		// requesting the authentication client to check for the validity of the token
		// and stores the response
		AuthenticationResponse validity = authenticationClient.getValidity(token);

		// if the requested user's role is customer
		if (authenticationClient.getRole(validity.getUserid()).equals("CUSTOMER")) {

			log.info("requesting Transaction service to fetch all transactions");
			return new ResponseEntity<>(this.transactionService.getTransactions(accountId, token), HttpStatus.OK);

			// if not, throws AccessDeniedException
		} else {
			log.error("Only customer can access this resource");
			throw new AccessDeniedException();
		}

	}

	/**
	 * @param token
	 * @param accountId
	 * @return ResponseEntity of List of ServiceCharge that contains a message,
	 *         accountId and balance for both accounts
	 * @throws TokenNotFoundException when the request header doesn't contains JWT
	 *                                token
	 * @throws AccessDeniedException  when the unauthorized customer or employee
	 *                                tries to access this resource
	 */
	@GetMapping("/getServiceCharges/{accountId}")
	public ResponseEntity<List<ServiceCharge>> getServiceCharges(
			@RequestHeader(value = "Authorization", required = false) String token,
			@PathVariable("accountId") String accountId) {

		// if token is null, throws TokenNotFoundException
		if (token == null) {
			log.error("JWT required for validating the access to this resource");
			throw new TokenNotFoundException();
		}

		// requesting the authentication client to check for the validity of the token
		// and stores the response
		AuthenticationResponse validity = authenticationClient.getValidity(token);

		// if the requested user's role is customer
		if (authenticationClient.getRole(validity.getUserid()).equals("CUSTOMER")) {

			log.info("requesting transaction service to fetch the service charges");
			return new ResponseEntity<>(this.transactionService.getServiceCharges(accountId, token), HttpStatus.OK);

			// if not, throws AccessDeniedException
		} else {
			log.error("Only customer can access this resource");
			throw new AccessDeniedException();
		}

	}

}