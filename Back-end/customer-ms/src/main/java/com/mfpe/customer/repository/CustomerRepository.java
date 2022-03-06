package com.mfpe.customer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mfpe.customer.entity.Customer;

/**
 * Repository Layer interface for Customer Microservice
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {

	/**
	 * Custom query method for fetching the customer based on given PAN no
	 */
	@Query("Select c from Customer c where c.panNo = ?1")
	Optional<Customer> findByPanNo(String panNo);

	/**
	 * Custom query method for fetching the customer who has max id
	 */
	@Query(value = "SELECT max(id) from Customer")
	long max();

	/**
	 * Custom query method for fetching the customer based on given customer ID
	 */
	@Query("Select c from Customer c where c.customerId = ?1")
	Optional<Customer> findByCustomerId(String customerId);

}
