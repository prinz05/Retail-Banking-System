package com.mfpe.customer.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mfpe.customer.dto.AccountDto;
import com.mfpe.customer.dto.CustomerCreationStatus;
import com.mfpe.customer.dto.CustomerDTO;
import com.mfpe.customer.entity.Customer;
import com.mfpe.customer.feign.AuthenticationFeign;
import com.mfpe.customer.model.AuthenticationResponse;
import com.mfpe.customer.model.Gender;
import com.mfpe.customer.service.CustomerService;

@WebMvcTest
class CustomerControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CustomerService customerService;

	@MockBean
	private AuthenticationFeign authenticationClient;

	private Customer customer;
	private Date dateOfBirth;
	private AuthenticationResponse validAuthResponseEmployee;
	private AuthenticationResponse invalidAuthResponse;
	private AuthenticationResponse validAuthResponseCustomer;
	private CustomerCreationStatus customerCreationStatus;
	private AccountDto account;
	private List<AccountDto> accounts;
	private CustomerDTO customerDto;
	private AuthenticationResponse authResponse;

	@BeforeEach
	void setUp() throws ParseException {

		customer = new Customer();
		dateOfBirth = new SimpleDateFormat("yyyy-MM-dd").parse("2005-12-12");
		customer.setAddress("Hyderabad");
		customer.setCustomerId("SBCU000001");
		customer.setCustomerName("Ram");
		customer.setDateOfBirth(dateOfBirth);
		customer.setGender(Gender.MALE);
		customer.setPanNo("ABCDE1234F");
		customer.setPassword("Ram@12345");
		validAuthResponseEmployee = new AuthenticationResponse("Ram", "SBEM000001", true);
		invalidAuthResponse = new AuthenticationResponse("Ram", "SBCU000001", false);
		validAuthResponseCustomer = new AuthenticationResponse("Ram", "SBCU000001", true);
		customerCreationStatus = new CustomerCreationStatus("Customer Created Successfully", "SBCU000001");
		account = new AccountDto("SBACS000001", 0.0);
		accounts = new ArrayList<AccountDto>();
		accounts.add(account);
		customerDto = new CustomerDTO("SBCU000001", "Ram", "Hyderabad", dateOfBirth, "ABCDE1234F", Gender.MALE,
				accounts);
		authResponse = new AuthenticationResponse("Ram", "SBCU000001", false);

	}

	@AfterEach
	void tearDown() {

		customer = null;
		dateOfBirth = null;
		validAuthResponseEmployee = null;
		invalidAuthResponse = null;
		validAuthResponseCustomer = null;
		customerCreationStatus = null;
		account = null;
		accounts = null;
		customerDto = null;
		authResponse = null;

	}

	@Test
	void testCreateCustomerWithoutHeader() throws JsonProcessingException, Exception {

		this.mockMvc.perform(post("/createCustomer").accept(MediaType.APPLICATION_JSON).content(asJsonString(customer))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized());

	}

	@Test
	void testCreateCustomerValid() throws Exception {

		when(authenticationClient.getValidity("token")).thenReturn(validAuthResponseEmployee);
		when(authenticationClient.getRole(validAuthResponseEmployee.getUserid())).thenReturn("EMPLOYEE");

		when(customerService.createCustomer(customer, "token")).thenReturn(customerCreationStatus);

		this.mockMvc
				.perform(post("/createCustomer").header("Authorization", "token").accept(MediaType.APPLICATION_JSON)
						.content(asJsonString(customer)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

	}

	@Test
	void testCreationCustomerInvalid() throws Exception {

		when(authenticationClient.getValidity("token")).thenReturn(invalidAuthResponse);
		when(authenticationClient.getRole(invalidAuthResponse.getUserid())).thenReturn("CUSTOMER");

		this.mockMvc
				.perform(post("/createCustomer").header("Authorization", "token").accept(MediaType.APPLICATION_JSON)
						.content(asJsonString(customer)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());

	}

	@Test
	void testGetCustomerDetailsInvalidToken() throws Exception {

		this.mockMvc.perform(get("/getCustomerDetails/{customerId}", "SBCU000001"))
				.andExpect(status().isUnauthorized());

	}

	@Test
	void testGetCustomerDetailsValid() throws Exception {

		when(authenticationClient.getValidity("token")).thenReturn(validAuthResponseCustomer);

		when(customerService.getCustomerDetails("SBCU000001", "SBCU000001")).thenReturn(customerDto);

		this.mockMvc.perform(get("/getCustomerDetails/{customerId}", "SBCU000001").header("Authorization", "token"))
				.andExpect(status().isOk());

	}

	@Test
	void testGetCustomerDetailsNotValid() throws Exception {

		when(authenticationClient.getValidity("token")).thenReturn(authResponse);

		when(customerService.getCustomerDetails("SBCU000001", "SBCU000002")).thenReturn(customerDto);

		this.mockMvc.perform(get("/getCustomerDetails/{customerId}", "SBCU000001").header("Authorization", "token"))
				.andExpect(status().isUnauthorized());

	}

	private static String asJsonString(final Object obj) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(obj);
	}

}