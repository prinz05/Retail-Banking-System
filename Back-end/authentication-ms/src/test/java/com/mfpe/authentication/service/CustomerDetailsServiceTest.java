package com.mfpe.authentication.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.mfpe.authentication.entity.AppUser;
import com.mfpe.authentication.repository.UserRepository;

@SpringBootTest
class CustomerDetailsServiceTest {
	
	@InjectMocks
	private CustomerDetailsService customerDetailsService;
	
	@Mock
	private UserRepository userRepo;
	
	private AppUser appUser;
	
	@BeforeEach
	void setUp() {
		
		appUser = new AppUser("SBEM000001", "emp", "ZW1w", null, "EMPLOYEE");

	}
	
	@AfterEach
	void tearDown() {
		
		appUser = null;
		
	}
	
	@Test
	void testLoadUserByUsername() {
		
		
		when(userRepo.findById("SBEM000001")).thenReturn(Optional.of(appUser));
		
		UserDetails loadUserByUsername2 = customerDetailsService.loadUserByUsername("SBEM000001");
		
		assertEquals(appUser.getUserid(), loadUserByUsername2.getUsername());
		
	}
	
	@Test
	void testLoadUserByUsernameInvalid() {
		
		when(userRepo.findById("SBEM000002")).thenReturn(Optional.empty());
		
		assertThrows(UsernameNotFoundException.class, () -> customerDetailsService.loadUserByUsername("SBEM000002"));
		
	}
	
	
	
	

}