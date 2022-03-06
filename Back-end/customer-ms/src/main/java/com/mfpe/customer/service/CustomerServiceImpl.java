package com.mfpe.customer.service;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mfpe.customer.dto.AccountDto;
import com.mfpe.customer.dto.CustomerCreationStatus;
import com.mfpe.customer.dto.CustomerDTO;
import com.mfpe.customer.entity.Customer;
import com.mfpe.customer.exception.CustomerAlreadyExistException;
import com.mfpe.customer.exception.CustomerNotFoundException;
import com.mfpe.customer.feign.AccountFeign;
import com.mfpe.customer.feign.AuthenticationFeign;
import com.mfpe.customer.model.AppUser;
import com.mfpe.customer.repository.CustomerRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {

	/**
	 * Service Layer Class for Customer Microservice
	 */

	// autowiring the Customer Repository
	@Autowired
	private CustomerRepository customerRepository;

	// autowiring the Account Feign Client
	@Autowired
	private AccountFeign accountFeign;

	// autowiring the Authentication Feign Client
	@Autowired
	private AuthenticationFeign authenticationClient;

	private Base64.Encoder encoder = Base64.getEncoder();

	/**
	 * @param customer
	 * @param token
	 * @return CustomerCreationStatus which contains a success message and created
	 *         Customer IDs
	 * @throws CustomerAlreadyExistException when the employee tries to create the
	 *                                       customer who already exist.
	 */
	@Override
	public CustomerCreationStatus createCustomer(Customer customer, String token) {

		log.info("creating the customer");

		String custId = "SBCU";

		// fetching the customer by given PAN no
		Optional<Customer> cust = customerRepository.findByPanNo(customer.getPanNo());

		// if customer is not present
		if (!cust.isPresent()) {

			// fetches all customers
			List<Customer> customerList = customerRepository.findAll();

			// if no customers perviously existed
			if (customerList.isEmpty()) {

				String formattedStr = String.format("%06d", 1);
				custId += formattedStr;

				Customer newCustomer = createCustomerObject(customer, custId);

				AppUser appUser = new AppUser(newCustomer.getCustomerId(), newCustomer.getCustomerName(),
						encoder.encodeToString(newCustomer.getPassword().getBytes()), null, "CUSTOMER");

				// requesting authentication client to create the user
				authenticationClient.createUser(appUser);

				// persists the customer
				customerRepository.save(newCustomer);
				log.info("created customer successfully");
				log.info("requesting account microservice to create accounts");
				accountFeign.createAccount(custId);
				log.info("accounts created successfully");

				// if few customers existed previously
			} else {

				// repository returns the max id among previously existed customers
				long id = customerRepository.max();
				String formattedStr = String.format("%06d", id + 1);

				custId += formattedStr;

				Customer newCustomer = createCustomerObject(customer, custId);

				AppUser appUser = new AppUser(newCustomer.getCustomerId(), newCustomer.getCustomerName(),
						encoder.encodeToString(newCustomer.getPassword().getBytes()), null, "CUSTOMER");

				// requesting authentication client to create the user
				authenticationClient.createUser(appUser);

				// persists the customer
				customerRepository.save(newCustomer);
				log.info("created customer successfully");
				log.info("requesting account microservice to create accounts");
				accountFeign.createAccount(custId);
				log.info("accounts created successfully");
			}
			return new CustomerCreationStatus("Customer Created Successfully", custId);
			// if customer is present, throws CustomerAlreadyExistException
		} else {
			log.error("Customer with given pan no is already existed");
			throw new CustomerAlreadyExistException();
		}

	}

	/**
	 * @param customer
	 * @param customerId
	 * @return Customer that contains all details
	 **/
	private Customer createCustomerObject(Customer customer, String custId) {
		Customer newCustomer = new Customer();
		newCustomer.setCustomerId(custId);
		newCustomer.setAddress(customer.getAddress());
		newCustomer.setCustomerName(customer.getCustomerName());
		newCustomer.setDateOfBirth(customer.getDateOfBirth());
		newCustomer.setGender(customer.getGender());
		newCustomer.setPanNo(customer.getPanNo());
		newCustomer.setPassword(customer.getPassword());
		return newCustomer;
	}

	/**
	 * @param customerId
	 * @return CustomerDto of the customer which contains Customer Id, Name,
	 *         Address, Date Of Birth, PAN Number, Gender, Account Type and
	 *         respective accountId, account's balance
	 * @throws CustomerNotFoundException when the user requests for the Customer who
	 *                                   doesn't exist
	 */
	@Override
	public CustomerDTO getCustomerDetails(String customerId, String custId) {

		// if requested customer requests his details or employee requests
		if (customerId.equals(custId) || custId.contains("EM")) {

			log.info("fetching customer details");

			// fetches the customer details according to the customer ID
			Optional<Customer> cust = customerRepository.findByCustomerId(customerId);

			// if customer is present
			if (cust.isPresent()) {

				log.info("requesting account microservice to fetch all accounts of the customer");
				ResponseEntity<List<AccountDto>> account = accountFeign.getCustomerAccounts(customerId);

				CustomerDTO customer = new CustomerDTO();
				customer.setCustomerId(customerId);
				customer.setAddress(cust.get().getAddress());
				customer.setCustomerName(cust.get().getCustomerName());
				customer.setDateOfBirth(cust.get().getDateOfBirth());
				customer.setGender(cust.get().getGender());
				customer.setPanNo(cust.get().getPanNo());
				customer.setAccounts(account.getBody());
				log.info("[Customer Details:] " + customer);
				return customer;

				// if not, throws CustomerNotFoundException
			} else {
				log.error("Customer with customer ID: " + customerId + "not found");
				throw new CustomerNotFoundException();
			}

			// if not, throws CustomerNotFoundException
		} else {
			log.error("Customer with customer ID: " + customerId + "not found");
			throw new CustomerNotFoundException();
		}
	}

}
