package com.mfpe.account.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.mfpe.account.model.TransactionsHistory;

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
@FeignClient(name = "transaction-Microservice", url = "${feign-transaction-url}")
public interface TransactionFeign {

	@GetMapping("/getTransactions/{accountId}")
	public ResponseEntity<List<TransactionsHistory>> getTransactions(
			@RequestHeader(value = "Authorization", required = false) String token,
			@PathVariable("accountId") String accountId);

}
