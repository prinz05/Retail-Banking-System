package com.mfpe.rule.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.mfpe.rule.model.AccountDto;

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
	public ResponseEntity<AccountDto> getAccount(@RequestHeader(value = "Authorization", required = false) String token,
			@PathVariable("accountId") String accountId);

	/**
	 * @param customerId
	 * @return List of ResponseEntity of AccountDto of the customer which contains
	 *         accountId and respective account's balance
	 */
	@GetMapping("/getCustomerAccounts/{customerId}")
	public ResponseEntity<List<AccountDto>> getCustomerAccounts(@PathVariable("customerId") String customerId);

}
