package com.mfpe.customer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mfpe.customer.dto.AccountDto;
import com.mfpe.customer.dto.CustomerCreationStatus;
import com.mfpe.customer.dto.CustomerDTO;
import com.mfpe.customer.entity.Customer;
import com.mfpe.customer.exception.CustomerAlreadyExistException;
import com.mfpe.customer.exception.CustomerNotFoundException;
import com.mfpe.customer.feign.AccountFeign;
import com.mfpe.customer.feign.AuthenticationFeign;
import com.mfpe.customer.model.Gender;
import com.mfpe.customer.repository.CustomerRepository;

@SpringBootTest
class CustomerServcieImplTest {

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private AuthenticationFeign authenticationClient;

	@Mock
	private AccountFeign accountFeign;

	@InjectMocks
	private CustomerServiceImpl customerServiceImpl;

	@BeforeEach
	void init() {
		MockitoAnnotations.openMocks(this);
	}

	private Customer customer1;
	private Customer customer2;
	private Date dateOfBirth;
	private CustomerCreationStatus customer1CreationStatus;
	private CustomerCreationStatus customer2CreationStatus;
	private String token;
	private List<Customer> customerList;
	private AccountDto account1;
	private AccountDto account2;
	private List<AccountDto> accountList;
	public String customerId;
	public String custId1;
	public String custId2;
	private CustomerDTO customerDto;

	@BeforeEach
	void setUp() throws ParseException {

		dateOfBirth = new SimpleDateFormat("yyyy-MM-dd").parse("2005-12-12");
		customer1 = new Customer();
		customer1.setAddress("Hyderabad");
		customer1.setCustomerId("SBCU000001");
		customer1.setCustomerName("Ram");
		customer1.setDateOfBirth(dateOfBirth);
		customer1.setGender(Gender.MALE);
		customer1.setPanNo("ABCDE1234F");
		customer1.setPassword("Ram@12345");
		customer2 = new Customer();
		customer2.setAddress("Hyderabad");
		customer2.setCustomerId("SBCU000002");
		customer2.setCustomerName("Ram");
		customer2.setDateOfBirth(dateOfBirth);
		customer2.setGender(Gender.MALE);
		customer2.setPanNo("ABCDE1234F");
		customer2.setPassword("Ram@12345");
		customer1CreationStatus = new CustomerCreationStatus("Customer Created Successfully", "SBCU000001");
		customer2CreationStatus = new CustomerCreationStatus("Customer Created Successfully", "SBCU000002");

		token = "token";
		customerList = new ArrayList<>();

		account1 = new AccountDto("SBACS000001", 0);
		account2 = new AccountDto("SBACC000001", 0);
		accountList = new ArrayList<>();
		accountList.add(account1);
		accountList.add(account2);

		customerId = "SBCU000001";
		custId1 = "SBCU000001";
		custId2 = "SBCU000002";

		customerDto = new CustomerDTO();
		customerDto.setAccounts(accountList);
		customerDto.setAddress(customer1.getAddress());
		customerDto.setCustomerId(customerId);
		customerDto.setCustomerName(customer1.getCustomerName());
		customerDto.setDateOfBirth(dateOfBirth);
		customerDto.setGender(Gender.MALE);
		customerDto.setPanNo(customer1.getPanNo());

	}

	@AfterEach
	void tearDown() {

		dateOfBirth = null;
		customer1 = null;
		customer2 = null;
		token = null;
		customerList = null;
		customer1CreationStatus = null;
		customer2CreationStatus = null;
		account1 = null;
		account2 = null;
		accountList = null;
		customerId = null;
		custId1 = null;
		custId2 = null;
		customerDto = null;

	}

	@Test
	void testCreateCustomerFirst() throws ParseException {

		when(customerRepository.findByPanNo("ABCDE1234G")).thenReturn(Optional.empty());
		when(customerRepository.findAll()).thenReturn(customerList);

		CustomerCreationStatus result = customerServiceImpl.createCustomer(customer1, token);
		assertThat(result.getCustomerId()).isEqualTo(customer1CreationStatus.getCustomerId());

	}

	@Test
	void testCreateCustomerRemaining() throws ParseException {

		customer1.setPanNo("ABCDE1234G");
		customerList.add(customer1);

		when(customerRepository.findByPanNo("ABCDE1234F")).thenReturn(Optional.empty());
		when(customerRepository.findAll()).thenReturn(customerList);
		when(customerRepository.max()).thenReturn(1L);

		CustomerCreationStatus result = customerServiceImpl.createCustomer(customer2, token);
		assertThat(result.getCustomerId()).isEqualTo(customer2CreationStatus.getCustomerId());

	}

	@Test
	void testCreateCustomerAlreadyExistException() throws ParseException {

		when(customerRepository.findByPanNo("ABCDE1234F")).thenReturn(Optional.of(customer1));
		assertThrows(CustomerAlreadyExistException.class, () -> customerServiceImpl.createCustomer(customer1, token));

	}

	@Test
	void testGetCustomerDetailsValid() throws ParseException {

		when(customerRepository.findByCustomerId(customerId)).thenReturn(Optional.of(customer1));

		ResponseEntity<List<AccountDto>> account = new ResponseEntity<>(accountList, HttpStatus.OK);
		when(accountFeign.getCustomerAccounts(customerId)).thenReturn(account);

		CustomerDTO result = customerServiceImpl.getCustomerDetails(customerId, custId1);

		assertThat(result.getCustomerId()).isEqualTo(customerDto.getCustomerId());
		
	}

	@Test
	void testGetCustomerDetailsValidByEmployee() throws ParseException {

		when(customerRepository.findByCustomerId(customerId)).thenReturn(Optional.of(customer1));

		ResponseEntity<List<AccountDto>> account = new ResponseEntity<>(accountList, HttpStatus.OK);
		when(accountFeign.getCustomerAccounts(customerId)).thenReturn(account);

		CustomerDTO result = customerServiceImpl.getCustomerDetails(customerId, "SBEM000001");

		assertThat(result.getCustomerId()).isEqualTo(customerDto.getCustomerId());
		
	}

	@Test
	void testGetCustomerDetailsInvalid() throws ParseException {

		when(customerRepository.findByCustomerId(customerId)).thenReturn(Optional.of(customer1));

		assertThrows(CustomerNotFoundException.class,
				() -> customerServiceImpl.getCustomerDetails(customerId, custId2));
		
	}

	@Test
	void testGetCustomerDetailsNotFoundException() {

		when(customerRepository.findByCustomerId(customerId)).thenReturn(Optional.empty());

		assertThrows(CustomerNotFoundException.class,
				() -> customerServiceImpl.getCustomerDetails(customerId, custId1));
		
	}

}