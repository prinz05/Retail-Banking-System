package com.mfpe.authentication.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

@SpringBootTest
class JwtUtilTest {

	@Mock
	Environment env;

	@InjectMocks
	JwtUtil jwtUtil;

	private UserDetails userDetails;
	private String generateToken;
	private Boolean validateToken;
	private String username;

	@BeforeEach
	void setUp() {

		userDetails = new User("admin", "admin", new ArrayList<>());
		generateToken = "";
		validateToken = false;
		username = "";

	}

	@AfterEach
	void tearDown() {

		userDetails = null;
		generateToken = null;
		validateToken = null;
		username = null;

	}

	@Test
	void generateTokenTest() {

		when(env.getProperty("set.expire.token")).thenReturn("30");
		generateToken = jwtUtil.generateToken(userDetails);
		assertNotNull(generateToken);

	}

	/**
	 * This method is used to test the token based on the given token and
	 * userDetails as parameter. First from the token we will extract the username
	 * and then will check in the database whether the token extracted username and
	 * the user residing in database is same or not and also will check whether the
	 * token has been expired or not
	 * 
	 * 
	 */
	@Test
	void validateTokenTest() {

		when(env.getProperty("set.expire.token")).thenReturn("30");
		generateToken = jwtUtil.generateToken(userDetails);
		validateToken = jwtUtil.validateToken(generateToken);
		assertEquals(true, validateToken);

	}

	/**
	 * to test the validity of token with name
	 */
	@Test
	void validateTokenWithNameTest() {

		when(env.getProperty("set.expire.token")).thenReturn("30");
		generateToken = jwtUtil.generateToken(userDetails);
		validateToken = jwtUtil.validateToken(generateToken);
		assertEquals(true, validateToken);

	}

	/**
	 * to test the validity of token with falseS name
	 */
	@Test
	void validateTokenWithNameFalseTest() {

		when(env.getProperty("set.expire.token")).thenReturn("30");
		validateToken = jwtUtil.validateToken("token");
		assertEquals(false, validateToken);

	}

	@Test
	void extractUsernameTest() {

		generateToken = jwtUtil.generateToken(userDetails);
		username = jwtUtil.extractUsername(generateToken);
		assertEquals("admin", username);

	}

}