package com.mfpe.authentication.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.mfpe.authentication.dto.AuthenticationResponse;
import com.mfpe.authentication.entity.AppUser;
import com.mfpe.authentication.repository.UserRepository;

@SpringBootTest
class ValidationserviceTest {

	@Mock
	private JwtUtil jwtutil;

	@Mock
	private UserRepository userRepo;
	
	@InjectMocks
	private ValidationService validationservice;

	private AuthenticationResponse validAuthResponse;
	private AuthenticationResponse invalidAuthResponse;
	private AppUser appUser;

	@BeforeEach
	void setUp() {

		validAuthResponse = new AuthenticationResponse("SBEM000001", "emp", true);
		invalidAuthResponse = new AuthenticationResponse("SBEM000001", "emp", false);
		appUser = new AppUser("SBEM000001", "emp", "ZW1w", null, "EMPLOYEE");

	}

	@AfterEach
	void tearDown() {

		validAuthResponse = null;
		invalidAuthResponse = null;
		appUser = null;

	}

	@Test
	void testValidate() {

		when(jwtutil.validateToken("token")).thenReturn(true);

		when(jwtutil.extractUsername("token")).thenReturn("SBEM000001");
		when(userRepo.findById("SBEM000001")).thenReturn(Optional.of(appUser));

		AuthenticationResponse auth = validationservice.validate("Bearer token");

		assertThat(validAuthResponse.getUserid()).isEqualTo(auth.getUserid());

	}

	@Test
	void testValidateInvalid() {

		when(jwtutil.validateToken("token")).thenReturn(false);

		AuthenticationResponse auth = validationservice.validate("Bearer token");

		assertThat(invalidAuthResponse.isValid()).isEqualTo(auth.isValid());

	}

}
