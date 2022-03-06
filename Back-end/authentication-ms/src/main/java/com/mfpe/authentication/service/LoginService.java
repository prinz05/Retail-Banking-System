package com.mfpe.authentication.service;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.mfpe.authentication.entity.AppUser;
import com.mfpe.authentication.exceptionhandling.AppUserNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LoginService {

	/**
	 * Service Layer Class for Authentication Microservice
	 */

	// autowiring JwtUtil
	@Autowired
	private JwtUtil jwtutil;

	// autowiring CustomerDetailsService
	@Autowired
	private CustomerDetailsService customerDetailservice;

	private Base64.Encoder encoder = Base64.getEncoder();

	/**
	 * @param appUser
	 * @return AppUser that contains user details along with token
	 * @throws AppUserNotFoundException when provided user details doesn't exist
	 */
	public AppUser userLogin(AppUser appuser) throws AppUserNotFoundException {

		// requesting customerDetailservice to fetch user details by userId
		final UserDetails userdetails = customerDetailservice.loadUserByUsername(appuser.getUserid());
		String userid = "";
		String role = "";
		String token = "";

		log.info("Password From DB-->{}", userdetails.getPassword());
		log.info("Password From Request-->{}", encoder.encodeToString(appuser.getPassword().getBytes()));

		// if provided credentials matches with existed credentials
		if ((userdetails.getPassword().equals(encoder.encodeToString(appuser.getPassword().getBytes())))) {
			userid = appuser.getUserid();
			role = appuser.getRole();

			// checks whether userId and role name matches the criteria
			if ((userid.contains("EM") && role.equalsIgnoreCase("EMPLOYEE"))
					|| (userid.contains("CU") && role.equalsIgnoreCase("CUSTOMER"))) {
				token = jwtutil.generateToken(userdetails);

				return new AppUser(userid, null, null, token, role);

				// if not, throws AppUserNotFoundException
			} else {
				log.error("Role and userId mismatch");
				throw new AppUserNotFoundException("Given Role is not for this User Id");
			}

			// if not, throws AppUserNotFoundException
		} else {
			log.error("Invalid Credentials");
			throw new AppUserNotFoundException("Username/Password is incorrect...Please check");
		}
	}
}