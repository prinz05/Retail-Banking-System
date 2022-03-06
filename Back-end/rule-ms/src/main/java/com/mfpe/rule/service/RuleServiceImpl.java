package com.mfpe.rule.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mfpe.rule.dto.RuleStatus;
import com.mfpe.rule.dto.ServiceCharge;
import com.mfpe.rule.exception.TokenNotFoundException;
import com.mfpe.rule.feign.AccountFeign;
import com.mfpe.rule.model.AccountDto;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RuleServiceImpl implements RuleService {

	/**
	 * Service Layer class for Rule Microservice
	 */

	// autowiring feign client of the Account Microservice
	@Autowired
	private AccountFeign accountClient;

	/**
	 * @param token
	 * @param accountId
	 * @param amount
	 * @return RuleStatus that contains a status and message about the current
	 *         transaction
	 */
	@Override
	public RuleStatus evaluateMinBal(double amount, String accountId, String token) {

		// if token is null, throws TokenNotFoundException
		if (token == null) {
			throw new TokenNotFoundException();
		}

		int savingsMin = 5000;
		int currentMin = 11000;

		// requesting the Account feign to get account details
		ResponseEntity<AccountDto> account = accountClient.getAccount(token, accountId);

		double balance = account.getBody().getBalance();

		// if account is savings type
		if (accountId.contains("SBACS")) {

			// deduct the amount from balance, but this deduction changes will not get
			// persisted
			double diff = balance - amount;

			if (diff >= savingsMin) {

				log.info("The transaction is accepted");
				return new RuleStatus("Allowed", "Completely OK");

			} else if (diff >= 0) {

				log.warn("The transaction is partially accepted");
				return new RuleStatus("Allowed", "Partially OK");

			} else {

				log.error("The transaction is denied");
				return new RuleStatus("Denied", "Not OK");
			}

		} else {

			double diff = balance - amount;

			if (diff >= currentMin) {
				log.info("The transaction is accepted");
				return new RuleStatus("Allowed", "Completely OK");

			} else if (diff >= 0) {
				log.warn("The transaction is partially accepted");
				return new RuleStatus("Allowed", "Partially OK");

			} else {
				log.error("The transaction is denied");
				return new RuleStatus("Denied", "Not OK");
			}
		}

	}

	/**
	 * @param token
	 * @param customerId
	 * @return List of ServiceCharge that contains account Id, message and balance
	 *         about both accounts of requested customer
	 */
	@Override
	public List<ServiceCharge> getServiceCharges(String customerId, String token) {

		// if token is null, throws TokenNotFoundException
		if (token == null) {
			log.error("JWT required for validating the access to this resource");
			throw new TokenNotFoundException();
		}

		// requesting Account client to get customer account details
		ResponseEntity<List<AccountDto>> accountList = accountClient.getCustomerAccounts(customerId);

		String savingsAccountId = accountList.getBody().get(0).getAccountId();
		String currentAccountId = accountList.getBody().get(1).getAccountId();

		double savingsAccountBalance = accountList.getBody().get(0).getBalance();
		double currentAccountBalance = accountList.getBody().get(1).getBalance();

		ServiceCharge savingsAccountCharges = new ServiceCharge();
		ServiceCharge currentAccountCharges = new ServiceCharge();

		// if savings balance is less than 5000
		if (savingsAccountBalance < 5000) {

			savingsAccountCharges.setAccountId(savingsAccountId);
			savingsAccountCharges.setMessage(
					"Your Savings Account is not satisfying the minimum balance criteria, ₹200 will be detucted.");
			savingsAccountCharges.setBalance(savingsAccountBalance);

			// if savings balance is greater than 5000
		} else {

			savingsAccountCharges.setAccountId(savingsAccountId);
			savingsAccountCharges.setMessage("maintaining minimum amount, no detection");
			savingsAccountCharges.setBalance(savingsAccountBalance);
		}

		// if current balance is less than 11000
		if (currentAccountBalance < 11000) {

			currentAccountCharges.setAccountId(currentAccountId);
			currentAccountCharges.setMessage(
					"Your Current Account is not satisfying the minimum balance criteria, ₹800 will be detucted.");
			currentAccountCharges.setBalance(currentAccountBalance);

			// if current balance is greater than 11000
		} else {

			currentAccountCharges.setAccountId(currentAccountId);
			currentAccountCharges.setMessage("maintaining minimum amount, no detection");
			currentAccountCharges.setBalance(currentAccountBalance);

		}

		List<ServiceCharge> serviceChargesList = new ArrayList<>();

		serviceChargesList.add(savingsAccountCharges);
		serviceChargesList.add(currentAccountCharges);

		return serviceChargesList;

	}

}
