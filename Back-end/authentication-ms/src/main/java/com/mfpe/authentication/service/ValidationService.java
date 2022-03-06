package com.mfpe.authentication.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mfpe.authentication.dto.AuthenticationResponse;
import com.mfpe.authentication.entity.AppUser;
import com.mfpe.authentication.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ValidationService {

	/**
	 * Service Layer Class for Authentication Microservice
	 */

	// autowiring JwtUtil
	@Autowired
	private JwtUtil jwtutil;

	// autowiring User repository
	@Autowired
	private UserRepository userRepo;

	/**
	 * @param token
	 * @return AuthenticationResponse that contains userid, name and validity status
	 */
	public AuthenticationResponse validate(String token) {

		AuthenticationResponse authenticationResponse = new AuthenticationResponse();

		String jwt = token.substring(7);

		// if token is valid
		if (jwtutil.validateToken(jwt)) {
			authenticationResponse.setUserid(jwtutil.extractUsername(jwt));
			authenticationResponse.setValid(true);
			Optional<AppUser> user = userRepo.findById(jwtutil.extractUsername(jwt));
			if (user.isPresent()) {
				authenticationResponse.setName(user.get().getUsername());
			}

			// if not, sets validity as false
		} else {
			authenticationResponse.setValid(false);
		}

		log.info("[Authentication Response:] " + authenticationResponse);
		return authenticationResponse;

	}
}