package com.mfpe.authentication.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mfpe.authentication.entity.AppUser;
import com.mfpe.authentication.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomerDetailsService implements UserDetailsService {

	/**
	 * Service Layer Class for Authentication Microservice
	 */

	// autowiring User repository
	@Autowired
	private UserRepository userRepo;

	/**
	 * @param userId
	 * @return UserDetails
	 */
	@Override
	public UserDetails loadUserByUsername(String userid) {

		// fetches the user by userId
		Optional<AppUser> appUser = userRepo.findById(userid);

		// returns the user object that contains required details if appUser is present
		if (appUser.isPresent()) {
			List<GrantedAuthority> grantedAuthorities = AuthorityUtils
					.commaSeparatedStringToAuthorityList("ROLE_" + appUser.get().getRole());
			return new User(appUser.get().getUserid(), appUser.get().getPassword(), grantedAuthorities);

			// if not, throws UsernameNotFoundException
		} else {
			log.error("Invalid credentials");
			throw new UsernameNotFoundException("Username/Password is Invalid...Please Check");
		}
	}
}
