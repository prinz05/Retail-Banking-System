package com.mfpe.transaction.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.mfpe.transaction.model.RuleStatus;
import com.mfpe.transaction.model.ServiceCharge;

@FeignClient(name = "Rule-Microservice", url = "${feign-rule-url}")
public interface RuleFeign {

	/**
	 * @param token
	 * @param accountId
	 * @param amount
	 * @return ResponseEntity of RuleStatus that contains a status and message about
	 *         the current transaction
	 */
	@GetMapping("/evaluateMinBal/{accountId}/{amount}")
	public ResponseEntity<RuleStatus> evaluateMinBal(
			@RequestHeader(value = "Authorization", required = false) String token,
			@PathVariable("accountId") String accountId, @PathVariable("amount") double amount);

	/**
	 * @param token
	 * @param accountId
	 * @return List of ResponseEntity of ServiceCharge that contains account Id,
	 *         message and balance about both accounts of requested customer
	 */
	@GetMapping("/getServiceCharges/{accountId}")
	public ResponseEntity<List<ServiceCharge>> getServiceCharges(
			@RequestHeader(value = "Authorization", required = false) String token,
			@PathVariable("accountId") String accountId);
}
