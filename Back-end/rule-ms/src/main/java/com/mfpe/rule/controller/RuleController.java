package com.mfpe.rule.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.mfpe.rule.dto.RuleStatus;
import com.mfpe.rule.dto.ServiceCharge;
import com.mfpe.rule.service.RuleService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class RuleController {

	/**
	 * Controller layer class for Rule Microservice
	 */
	@Autowired
	private RuleService ruleService;

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
			@PathVariable("accountId") String accountId, @PathVariable("amount") double amount) {

		log.info("requesting rule service to evaluate the amount with minimum balance criteria");
		return new ResponseEntity<>(this.ruleService.evaluateMinBal(amount, accountId, token), HttpStatus.OK);

	}

	/**
	 * @param token
	 * @param accountId
	 * @return List of ResponseEntity of ServiceCharge that contains account Id,
	 *         message and balance about both accounts of requested customer
	 */
	@GetMapping("/getServiceCharges/{accountId}")
	public ResponseEntity<List<ServiceCharge>> getServiceCharges(
			@RequestHeader(value = "Authorization", required = false) String token,
			@PathVariable("accountId") String accountId) {

		log.info("requesting rule service to calculate the service charges");
		return new ResponseEntity<>(this.ruleService.getServiceCharges(accountId, token), HttpStatus.OK);

	}

}
