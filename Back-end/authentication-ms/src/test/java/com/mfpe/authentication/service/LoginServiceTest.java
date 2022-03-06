package com.mfpe.authentication.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.mfpe.authentication.entity.AppUser;
import com.mfpe.authentication.exceptionhandling.AppUserNotFoundException;

@SpringBootTest
class LoginServiceTest {

	@Mock
	private JwtUtil jwtutil;

	@Mock
	private CustomerDetailsService customerDetailservice;

	@InjectMocks
	private LoginService loginService;

	private UserDetails validEmployeeUserDetails;
	private AppUser validEmployeeAppUser;
	private AppUser employeeWithCustomerRole;
	private AppUser customerWithEmployeeRole;
	private AppUser validCustomerAppUser;
	private UserDetails validCustomerUserDetails;
	private AppUser employeeWithInvalidPassword;

	@BeforeEach
	void setUp() {

		validEmployeeAppUser = new AppUser("SBEM000001", "emp", "emp", null, "EMPLOYEE");
		validEmployeeUserDetails = new User("SBEM000001", "ZW1w", new ArrayList<>());
		employeeWithCustomerRole = new AppUser("SBEM000001", "emp", "emp", null, "CUSTOMER");
		customerWithEmployeeRole = new AppUser("SBCU000000", "cust", "cust", null, "EMPLOYEE");
		validCustomerAppUser = new AppUser("SBCU000000", "cust", "cust", null, "CUSTOMER");
		validCustomerUserDetails = new User("SBCU000000", "Y3VzdA==", new ArrayList<>());
		employeeWithInvalidPassword = new AppUser("SBEM000001", "emp", "cust", null, "EMPLOYEE");

	}

	@AfterEach
	void tearDown() {

		validEmployeeAppUser = null;
		validEmployeeUserDetails = null;
		employeeWithCustomerRole = null;
		customerWithEmployeeRole = null;
		validCustomerAppUser = null;
		validCustomerUserDetails = null;
		employeeWithInvalidPassword = null;

	}

	@Test
	void testUserLogin() throws AppUserNotFoundException {

		when(customerDetailservice.loadUserByUsername("SBEM000001")).thenReturn(validEmployeeUserDetails);

		when(jwtutil.generateToken(validEmployeeUserDetails)).thenReturn("token");

		AppUser appUser1 = new AppUser("SBEM000001", null, null, "token", "EMPLOYEE");

		AppUser appUser2 = loginService.userLogin(validEmployeeAppUser);

		assertThat(appUser1.getUserid()).isEqualTo(appUser2.getUserid());

	}

	@Test
	void testUserLoginInvalidRole() {

		when(customerDetailservice.loadUserByUsername("SBEM000001")).thenReturn(validEmployeeUserDetails);

		assertThrows(AppUserNotFoundException.class, () -> loginService.userLogin(employeeWithCustomerRole));

	}

	@Test
	void testUserLoginInvalidUserId() {

		when(customerDetailservice.loadUserByUsername("SBCU000000")).thenReturn(validEmployeeUserDetails);

		assertThrows(AppUserNotFoundException.class, () -> loginService.userLogin(customerWithEmployeeRole));

	}

	@Test
	void testUserLoginCustomer() throws AppUserNotFoundException {

		when(customerDetailservice.loadUserByUsername("SBCU000000")).thenReturn(validCustomerUserDetails);

		when(jwtutil.generateToken(validCustomerUserDetails)).thenReturn("token");

		AppUser appUser1 = new AppUser("SBCU000000", null, null, "token", "CUSTOMER");

		AppUser appUser2 = loginService.userLogin(validCustomerAppUser);

		assertThat(appUser1.getUserid()).isEqualTo(appUser2.getUserid());

	}

	@Test
	void testUserLoginInvalidCustomerRole() {

		when(customerDetailservice.loadUserByUsername("SBCU000000")).thenReturn(validCustomerUserDetails);

		assertThrows(AppUserNotFoundException.class, () -> loginService.userLogin(customerWithEmployeeRole));

	}

	@Test
	void testUserLoginInvalidCustomerUserId() {

		when(customerDetailservice.loadUserByUsername("SBEM000001")).thenReturn(validEmployeeUserDetails);

		assertThrows(AppUserNotFoundException.class, () -> loginService.userLogin(employeeWithCustomerRole));

	}

	@Test
	void testUserLoginInvalidPassword() {

		when(customerDetailservice.loadUserByUsername("SBEM000001")).thenReturn(validEmployeeUserDetails);

		assertThrows(AppUserNotFoundException.class, () -> loginService.userLogin(employeeWithInvalidPassword));

	}

}