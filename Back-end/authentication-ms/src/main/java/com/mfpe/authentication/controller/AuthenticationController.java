package com.mfpe.authentication.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.mfpe.authentication.dto.AuthenticationResponse;
import com.mfpe.authentication.entity.AppUser;
import com.mfpe.authentication.exceptionhandling.AppUserNotFoundException;
import com.mfpe.authentication.repository.UserRepository;
import com.mfpe.authentication.service.LoginService;
import com.mfpe.authentication.service.ValidationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
public class AuthenticationController {

	/**
	 * Controller Layer Class for Authentication Microservice
	 */

	// Autowiring User Repository
	@Autowired
	private UserRepository userRepository;

	// Autowiring Login Service
	@Autowired
	private LoginService loginService;

	// Autowiring Validation Service
	@Autowired
	private ValidationService validationService;

	/**
	 * @return ResponseEntity of String
	 */
	@GetMapping("/health")
	public ResponseEntity<String> healthCheckup() {

		log.info("Health Check for Authentication Microservice");
		log.info("health checkup ----->{}", "up");
		return new ResponseEntity<>("UP", HttpStatus.OK);

	}

	/**
	 * @param appUserloginCredentials
	 * @return ResponseEntity of AppUser that contains userid, username, password,
	 *         authToken and role
	 * @throws AppUserNotFoundException which will be raised when the user provides
	 *                                  invalid credentials or when the provided
	 *                                  role is different
	 */
	@PostMapping("/login")
	public ResponseEntity<AppUser> login(@RequestBody AppUser appUserloginCredentials) throws AppUserNotFoundException {

		// requesting login service to let the user login
		AppUser user = loginService.userLogin(appUserloginCredentials);
		log.info("Credentials ----->{}", user);
		return new ResponseEntity<>(user, HttpStatus.ACCEPTED);

	}

	/**
	 * @param token
	 * @return AuthenticationResponse that contains userid, name and validity status
	 */
	@GetMapping("/validateToken")
	public AuthenticationResponse getValidity(@RequestHeader("Authorization") final String token) {

		log.info("Token Validation ----->{}", token);
		return validationService.validate(token);

	}

	/**
	 * @param appUserCredentials
	 * @return ResponseEntity of AppUser that contains userid, username, password,
	 *         authToken and role
	 */
	@PostMapping("/createUser")
	public ResponseEntity<AppUser> createUser(@RequestBody AppUser appUserCredentials) {

		// saving the record of new customer
		AppUser createdUser = userRepository.save(appUserCredentials);

		log.info("user creation---->{}", createdUser);
		return new ResponseEntity<>(createdUser, HttpStatus.CREATED);

	}

	/**
	 * @param id
	 * @return String whose value can be CUSTOMER, EMPLOYEE or Role Not Found
	 */
	@GetMapping("/role/{id}")
	public String getRole(@PathVariable("id") String id) {

		// fetching the user by userId
		Optional<AppUser> user = this.userRepository.findById(id);

		// returns the user role is user is present
		if (user.isPresent()) {
			return user.get().getRole();

			// if not, returns the not found status
		} else {
			return "Role Not Found";
		}

	}

}