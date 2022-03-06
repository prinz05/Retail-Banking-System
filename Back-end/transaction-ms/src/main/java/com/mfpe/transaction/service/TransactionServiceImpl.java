package com.mfpe.transaction.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mfpe.transaction.dto.TransactionStatus;
import com.mfpe.transaction.entity.TransactionsHistory;
import com.mfpe.transaction.exception.InvalidAmountException;
import com.mfpe.transaction.exception.InvalidTransactionException;
import com.mfpe.transaction.exception.NotEnoughBalanceException;
import com.mfpe.transaction.feign.AccountFeign;
import com.mfpe.transaction.feign.RuleFeign;
import com.mfpe.transaction.model.AccountDto;
import com.mfpe.transaction.model.RuleStatus;
import com.mfpe.transaction.model.ServiceCharge;
import com.mfpe.transaction.repository.TransactionRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

	/**
	 * Service Layer Class for Transaction Microservice
	 */

	// autowiring the transaction repository
	@Autowired
	private TransactionRepository transactionRepo;

	// autowiring the account feign client
	@Autowired
	private AccountFeign accountClient;

	// autowiring the rule feign client
	@Autowired
	private RuleFeign ruleClient;

	/**
	 * @param accountId
	 * @param amount
	 * @re
	 */
	@Override
	public TransactionStatus deposit(String accountId, double amount) {

		log.info("requesting account microservice to deposit the money");

		// if amount is 0 or less than it, throws InvalidAmountException
		if (amount <= 0.0) {
			log.error("Amount should be greater than 0");
			throw new InvalidAmountException();
		}

		// requesting account mircroservice to deposit the money
		TransactionStatus status = this.accountClient.deposit(accountId, amount).getBody();
		log.info("[Transaction status:] " + status);

		return saveTransaction(accountId, amount, status, "Deposit", "-");

	}

	@Override
	public TransactionStatus withdraw(String accountId, double amount, String token) {

		log.info("requesting the rule microservice to validate the withdrawal");

		// if amount is 0 or less than it, throws InvalidAmountException
		if (amount <= 0.0) {
			log.error("Amount should be greater than 0");
			throw new InvalidAmountException();
		}

		// requesting rule microservice to evaluate min balance criteria
		RuleStatus ruleStatus = this.ruleClient.evaluateMinBal(token, accountId, amount).getBody();

		// requesting account mixroservice to get account details
		AccountDto accountDto = this.accountClient.getAccount(token, accountId).getBody();

		// if response from rule microservice is ok, then
		if (ruleStatus.getMessage().equalsIgnoreCase("Not Ok")) {

			log.info("permission denied for transaction, not enough balance");

			// denies the transaction
			TransactionStatus tStatus = new TransactionStatus("Denied", accountDto.getBalance(),
					accountDto.getBalance());
			log.info("[Transaction status:] " + tStatus);

			// save record and throw NotEnoughBalanceException
			saveTransaction(accountId, amount, tStatus, "Withdraw", "-");
			log.error("Not enough balance to proceed with this transaction");
			throw new NotEnoughBalanceException();

			// else request account microservice to withdraw money, save record and return
			// status
		} else {

			log.info("permission granted for transaction, requesting Account microservice to proceed further");
			TransactionStatus tStatus = this.accountClient.withdraw(token, accountId, amount).getBody();
			log.info("[Transaction status:] " + tStatus);
			return saveTransaction(accountId, amount, tStatus, "Withdraw", "-");

		}

	}

	@Override
	public TransactionStatus transfer(String sourceAccountId, String destinationAccountId, double amount,
			String token) {

		log.info("requesting the rule microservice to validate the transfer");

		// if amount is 0 or less than it, throws InvalidAmountException
		if (amount <= 0.0) {
			log.error("Amount should be greater than 0");
			throw new InvalidAmountException();
		}

		// if both account IDs are equal, then throws InvalidTransactionException
		if (sourceAccountId.equalsIgnoreCase(destinationAccountId)) {
			log.error("Both source and destination IDs should not be same");
			throw new InvalidTransactionException();
		}

		// requesting rule microservice to evaluate min blanace criteria
		RuleStatus ruleStatus = this.ruleClient.evaluateMinBal(token, sourceAccountId, amount).getBody();

		// requesting account microservice to get account details
		AccountDto sourceAccountDto = this.accountClient.getAccount(token, sourceAccountId).getBody();

		// if rule response status is not ok, then
		if (ruleStatus.getMessage().equalsIgnoreCase("Not Ok")) {

			log.info("permission denied for transaction, not enough balance");

			// saves record, shares denied status and throws NotEnoughBalanceException
			TransactionStatus tStatus = new TransactionStatus("Denied", sourceAccountDto.getBalance(),
					sourceAccountDto.getBalance());
			log.info("[Transaction status:] " + tStatus);
			saveTransaction(sourceAccountId, amount, tStatus, "Transfer - Unsent", destinationAccountId);
			log.error("Not enough balance to proceed with this transaction");
			throw new NotEnoughBalanceException();

			// else request account micro service to deposit and withdraw from destination
			// and source accounts respectively. At last, saves the record and shares the
			// response
		} else {
			log.info("permission granted for transaction, requesting Account microservice to proceed further");
			TransactionStatus sourceStatus = this.accountClient.withdraw(token, sourceAccountId, amount).getBody();
			TransactionStatus destinationStatus = this.accountClient.deposit(destinationAccountId, amount).getBody();
			log.info("[Source Transaction status:] " + sourceStatus);
			log.info("[Destination Transaction status:] " + destinationStatus);

			TransactionStatus finalStatus = saveTransaction(sourceAccountId, amount, sourceStatus, "Transfer - Sent",
					destinationAccountId);
			saveTransaction(sourceAccountId, amount, destinationStatus, "Transfer - Received", destinationAccountId);
			finalStatus.setMessage(finalStatus.getMessage().replace("withdraw", "transfer"));
			return finalStatus;
		}

	}

	@Override
	public List<TransactionsHistory> getTransactions(String accountId, String token) {

		log.info("validating details");

		// requesting account microservice to get account details
		this.accountClient.getAccount(token, accountId);
		log.info("account found! fetching the transaction history");

		// fetches the records from transaction repsitory and filters it according to
		// account Id and returns list
		List<TransactionsHistory> records = this.transactionRepo.findBySourceAccountIdOrDestinationAccountId(accountId,
				accountId);
		records = records.stream().filter(tRecord -> {
			String transactionType = tRecord.getTransactionType();
			String sourceId = tRecord.getSourceAccountId();
			return (((transactionType.equalsIgnoreCase("Withdraw") || transactionType.equalsIgnoreCase("Deposit")
					|| transactionType.equalsIgnoreCase("Transfer - Sent")
					|| transactionType.equalsIgnoreCase("Transfer - Unsent")) && accountId.equals(sourceId))
					|| (transactionType.equalsIgnoreCase("Transfer - Received") && !accountId.equals(sourceId)));
		}).collect(Collectors.toList());

		log.info("[Transactions history details:] " + records);
		return records;
	}

	@Override
	public List<ServiceCharge> getServiceCharges(String accountId, String token) {

		log.info("requesting Rule Microservice to get service charges");

		// requests rule microservice to get service charges and returns the list
		return this.ruleClient.getServiceCharges(token, accountId).getBody();

	}

	/**
	 * @param accountId
	 * @param amount
	 * @param status
	 * @param transactionType
	 * @param destinationAccountId
	 * @return TransactionStatus that contains a message, source balance and
	 *         destination balance
	 */
	private TransactionStatus saveTransaction(String accountId, double amount, TransactionStatus status,
			String transactionType, String destinationAccountId) {

		String transactionId = "SBTR-";

		if (!transactionType.contains("Received")) {
			transactionId += accountId.replaceAll("[^0-9]", "") + "-";
		} else {
			transactionId += destinationAccountId.replaceAll("[^0-9]", "") + "-";
		}

		List<TransactionsHistory> transactions = transactionRepo.findAll();

		// sets the transaction id as 1 if no records exist, else the id will be max + 1
		// (max is the maximum id among previously existing records)
		if (transactions.isEmpty()) {
			String formattedStr = String.format("%06d", 1);
			transactionId += formattedStr;
		} else {
			long id = transactionRepo.max();
			id += 1;
			String formattedStr = String.format("%06d", id);
			transactionId += formattedStr;
		}

		saveRecord(accountId, amount, status, transactionId, transactionType, destinationAccountId);
		return status;
	}

	/**
	 * @param accountId
	 * @param amount
	 * @param status
	 * @param transactionId
	 * @param transactionType
	 * @param destinationAccountId
	 * @return
	 */
	private void saveRecord(String accountId, double amount, TransactionStatus status, String transactionId,
			String transactionType, String destinationAccountId) {
		log.info("saving the record");

		// sets the values and persists the record
		TransactionsHistory tRecord = new TransactionsHistory();
		tRecord.setTransactionId(transactionId);
		tRecord.setTransactionType(transactionType);
		tRecord.setSourceAccountId(accountId);
		tRecord.setDestinationAccountId(destinationAccountId);
		tRecord.setAmount(amount);
		tRecord.setDateOfTransaction(new Date());

		// checks the status and sets the message accordingly
		if (status.getMessage().contains("Denied")) {
			tRecord.setTransactionStatus("Unsuccessful");
		} else
			tRecord.setTransactionStatus("Successful");

		tRecord.setSourceBalance(status.getSourceBalance());
		tRecord.setDestinationBalance(status.getDestinationBalance());
		log.info("[Transaction record details:] " + tRecord);
		this.transactionRepo.save(tRecord);
	}

}
