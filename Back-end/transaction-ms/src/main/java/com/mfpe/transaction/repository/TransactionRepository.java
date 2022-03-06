package com.mfpe.transaction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mfpe.transaction.entity.TransactionsHistory;

/**
 * Repository Layer Class for Transaction Microservice
 */
public interface TransactionRepository extends JpaRepository<TransactionsHistory, String> {

	/**
	 * Custom query method that returns the maximum id of all the records
	 */
	@Query(value = "SELECT max(id) from TransactionsHistory")
	long max();

	/**
	 * Custom query method that returns the Optional of transaction that related to the given id
	 */
	@Query("Select t.transactionId from TransactionsHistory t where t.id = ?1")
	Optional<TransactionsHistory> findById(long id);

	/**
	 * Query method that returns the List of all transactions that related to the given source & destination Ids
	 */
	List<TransactionsHistory> findBySourceAccountIdOrDestinationAccountId(String sourceAccountId,
			String destinationAccountId);

}
