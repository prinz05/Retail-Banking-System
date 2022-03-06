package com.mfpe.account.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mfpe.account.dto.AccountCreationStatus;
import com.mfpe.account.dto.AccountDto;
import com.mfpe.account.dto.Statement;
import com.mfpe.account.dto.TransactionStatus;
import com.mfpe.account.entity.Account;
import com.mfpe.account.exception.AccountNotFoundException;
import com.mfpe.account.exception.IncorrectDateInputException;
import com.mfpe.account.feign.TransactionFeign;
import com.mfpe.account.model.AccountType;
import com.mfpe.account.model.TransactionsHistory;
import com.mfpe.account.repository.AccountRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

	/**
	 * Service Layer class for Account Microservice
	 */

	// autowiring the Account Repository
	@Autowired
	private AccountRepository accountRepo;

	// autowiring the Transaction feign client
	@Autowired
	private TransactionFeign transactionClient;

	/**
	 * @param customerId
	 * @return AccountCreationStatus which contains a success message and created
	 *         account IDs
	 */
	@Override
	public AccountCreationStatus createAccount(String customerId) {

		log.info("Creating accounts");

		String savingsId = "SBACS" + customerId.replaceAll("[^0-9]", "");
		String currentId = "SBACC" + customerId.replaceAll("[^0-9]", "");

		Account savingsAccount = new Account(savingsId, customerId, 0.0, AccountType.SAVINGS);
		Account currentAccount = new Account(currentId, customerId, 0.0, AccountType.CURRENT);

		// persists the accounts in repository
		this.accountRepo.save(savingsAccount);
		this.accountRepo.save(currentAccount);

		log.info("Accounts successfully created");

		return new AccountCreationStatus(savingsId, currentId, "Successfully created savings & current accounts");

	}

	/**
	 * @param accountId
	 * @param customerId
	 * @return AccountDto which contains accountId and respective account's balance
	 * @throws AccountNotFoundException when the user requests for the account which
	 *                                  doesn't exist
	 */
	@Override
	public AccountDto getAccount(String accountId, String customerId) {

		log.info("fetching account details");

		// searches for the account with given account ID in the repository
		Optional<Account> account = this.accountRepo.findById(accountId);

		// if account is present
		if (account.isPresent()) {

			// checks whether the requested account belongs to the current customer or not
			if (customerId.equals(account.get().getCustomerId())) {
				log.info("[Account Details:] " + account.get());

				// if yes, returns account details that includes account ID and current balance
				return new AccountDto(account.get().getAccountId(), account.get().getBalance());

				// if the requested account doesn't belongs to the current customer, throws
				// AccountNotFoundException
			} else {

				log.error("Account with account ID: " + accountId + "not found");
				throw new AccountNotFoundException();
			}

			// if account is not present, throws AccountNotFoundException
		} else {
			
			log.error("Account with account ID: " + accountId + "not found");
			throw new AccountNotFoundException();
		}

	}

	/**
	 * @param customerId
	 * @return List of AccountDto of the customer which contains accountId and
	 *         respective account's balance
	 */
	@Override
	public List<AccountDto> getCustomerAccounts(String customerId) {

		log.info("fetching account details");

		// seraches for the accounts which are linked up with given customer ID
		List<Account> accounts = this.accountRepo.findByCustomerId(customerId);

		List<AccountDto> accountDtoList = new ArrayList<>();

		accounts.forEach(account -> accountDtoList.add(new AccountDto(account.getAccountId(), account.getBalance())));
		log.info("[Account Details:] " + accountDtoList);

		return accountDtoList;

	}

	/**
	 * @param accountId
	 * @param fromDate
	 * @param toDate
	 * @return Statement which contains current date, account ID, current balance
	 *         and the transaction history between the given date range
	 * @throws AccountNotFoundException when the user requests for the account which
	 *                                  doesn't exist
	 */
	@Override
	public Statement getAccountStatement(String accountId, Date fromDate, Date toDate, String token) {

		log.info("fetching transaction details");

		// if fromDate is after toDate, throws IncorrectDateInputException
		if (fromDate.after(toDate)) {
			log.error("Given date range is invalid, From Date should always be before To Date");
			throw new IncorrectDateInputException();
		}

		// searches for the account with given account ID in the repository
		Optional<Account> account = this.accountRepo.findById(accountId);

		// if account is present
		if (account.isPresent()) {

			log.info("creating the account statement");

			// transaction client will be requested to fetch all the transactions of this
			// particular account
			List<TransactionsHistory> records = this.transactionClient.getTransactions(token, accountId).getBody();

			// filters the transaction records according to the given date range
			records = records.stream()
					.filter(tRecord -> (!(tRecord.getDateOfTransaction().before(fromDate)
							|| tRecord.getDateOfTransaction().after(new Date(toDate.getTime() + 1000 * 60 * 60 * 24)))))
					.collect(Collectors.toList());

			// creates a statement that contains current date, account Id, current balance
			// and the transaction history
			Statement statement = new Statement();
			statement.setDate(new Date());
			statement.setAccountId(accountId);
			statement.setCurrentBalance(account.get().getBalance());
			statement.setHistory(records);
			log.info("[Account Statement Details:] " + statement);

			return statement;

			// if account is not present, throws AccountNotFoundException
		} else {
			log.error("Account with account ID: " + accountId + "not found");
			throw new AccountNotFoundException();
		}

	}

	/**
	 * @param accountId
	 * @param fromDate
	 * @param toDate
	 * @return Statement which contains current date, account ID, current balance
	 *         and the transaction history for the current month
	 * @throws AccountNotFoundException when the user requests for the account which
	 *                                  doesn't exist
	 */
	@SuppressWarnings("deprecation")
	@Override
	public Statement getAccountStatement(String accountId, String token) {

		log.info("fetching transaction details");

		// searches for the account with given account ID in the repository
		Optional<Account> account = this.accountRepo.findById(accountId);

		// if account is present
		if (account.isPresent()) {

			log.info("creating the account statement");

			// transaction client will be requested to fetch all the transactions of this
			// particular account
			List<TransactionsHistory> records = this.transactionClient.getTransactions(token, accountId).getBody();

			// filters the transaction records according to the current month
			records = records.stream().filter(
					tRecord -> tRecord.getDateOfTransaction().getMonth() == Calendar.getInstance().get(Calendar.MONTH))
					.collect(Collectors.toList());

			// creates a statement that contains current date, account Id, current balance
			// and the transaction history
			Statement statement = new Statement();
			statement.setDate(new Date());
			statement.setAccountId(accountId);
			statement.setCurrentBalance(account.get().getBalance());
			statement.setHistory(records);

			log.info("[Account Statement Details:] " + statement);

			return statement;

			// if account is not present, throws AccountNotFoundException
		} else {
			log.error("Account with account ID: " + accountId + "not found");
			throw new AccountNotFoundException();
		}

	}

	/**
	 * @param accountId
	 * @param amount
	 * @return TransactionStatus which contains a success/unsuccess message, source
	 *         balance & destination balance
	 * @throws AccountNotFoundException when the user requests for the account which
	 *                                  doesn't exist
	 */
	@Override
	public TransactionStatus deposit(String accountId, double amount) {

		log.info("Money deposit under process");

		// searches for the account with given account ID in the repository
		Optional<Account> account = this.accountRepo.findById(accountId);

		// if account is present
		if (account.isPresent()) {

			double sourceBalance = account.get().getBalance();
			account.get().setBalance(sourceBalance + amount);

			// saves again the account in repository to update it
			this.accountRepo.save(account.get());

			double destinationBalance = account.get().getBalance();

			// creates the status that contains success messgae, source balance and
			// destination balance
			TransactionStatus status = new TransactionStatus("Money deposit transaction successfull", sourceBalance,
					destinationBalance);

			log.info("[ " + accountId + " Transaction Details:] " + status);

			return status;

			// if account is not present, throws AccountNotFoundException
		} else {
			log.error("Account with account ID: " + accountId + "not found");
			throw new AccountNotFoundException();
		}

	}

	/**
	 * @param accountId
	 * @param amount
	 * @return TransactionStatus which contains a success/unsuccess message, source
	 *         balance & destination balance
	 * @throws AccountNotFoundException when the user requests for the account which
	 *                                  doesn't exist
	 */
	@Override
	public TransactionStatus withdraw(String accountId, double amount, String customerId) {

		log.info("Money withdraw under process");

		// searches for the account with given account ID in the repository
		Optional<Account> account = this.accountRepo.findById(accountId);

		// if account is present
		if (account.isPresent()) {

			String custId = account.get().getCustomerId();
			double sourceBalance = account.get().getBalance();

			// if account's customer ID matches with ID of requested customer
			if (custId.equals(customerId)) {

				account.get().setBalance(sourceBalance - amount);

				// saves the account again in repository to update it
				this.accountRepo.save(account.get());

				double destinationBalance = account.get().getBalance();

				// creates the status that contains success messgae, source balance and
				// destination balance
				TransactionStatus status = new TransactionStatus("Money withdraw transaction successfull",
						sourceBalance, destinationBalance);

				log.info("[ " + accountId + " Transaction Details:] " + status);

				return status;

				// if account's customer ID doesn't matches with ID of requested customer,
				// throws AccountNotFoundException
			} else {
				log.error("Account with account ID: " + accountId + "not found");
				throw new AccountNotFoundException();
			}

			// if account is not present, throws AccountNotFoundException
		} else {
			log.error("Account with account ID: " + accountId + "not found");
			throw new AccountNotFoundException();
		}

	}

}