package com.mfpe.transaction.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.mfpe.transaction.dto.TransactionStatus;
import com.mfpe.transaction.model.AccountDto;

@FeignClient(name = "Account-Microservice", url = "${feign-account-url}")
public interface AccountFeign {

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
	public ResponseEntity<AccountDto> getAccount(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable("accountId") String accountId);

	/**
	 * @param accountId
	 * @param amount
	 * @return ResponseEntity of TransactionStatus which contains a
	 *         success/unsuccess message, source balance & destination balance
	 */
	@PostMapping("/deposit/{accountId}/{amount}")
	public ResponseEntity<TransactionStatus> deposit(@PathVariable("accountId") String accountId,
			@PathVariable("amount") double amount);

	/**
	 * @param accountId
	 * @param amount
	 * @return ResponseEntity of TransactionStatus which contains a
	 *         success/unsuccess message, source balance & destination balance
	 * @throws TokenNotFoundException when request header doesn't contains JWT token
	 */
	@PostMapping("/withdraw/{accountId}/{amount}")
	public ResponseEntity<TransactionStatus> withdraw(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable("accountId") String accountId,
			@PathVariable("amount") double amount);

}
