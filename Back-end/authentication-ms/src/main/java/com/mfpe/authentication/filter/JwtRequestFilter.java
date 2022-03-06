package com.mfpe.authentication.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mfpe.authentication.service.CustomerDetailsService;
import com.mfpe.authentication.service.JwtUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

	/**
	 * Request Filter for Authentication Microservice
	 */

	// autowiring JwtUtil
	@Autowired
	private JwtUtil jwtUtil;

	// autowiring CustomerDetailsService
	@Autowired
	private CustomerDetailsService customerDetailsService;

	/**
	 * @param request
	 * @param response
	 * @param filterChain
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		final String authHeadder = request.getHeader("Authorization");

		String username = null;
		String jwt = null;

		// if authorization is not null and starts with "Bearer "
		if (authHeadder != null && authHeadder.startsWith("Bearer ")) {

			jwt = authHeadder.substring(7);
			username = jwtUtil.extractUsername(jwt);

		}

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

			// fetches the user details by requesting customerDetailsService
			UserDetails userDetails = this.customerDetailsService.loadUserByUsername(username);

			// if token is valid
			if (jwtUtil.validateToken(jwt)) {

				log.info("Setting token for authentication using request");
				UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, null,
						userDetails.getAuthorities());
				token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(token);
				log.info("Finished Setting token in Security Context holder for authentication using request");

			}
		}

		log.info("Chaining the filter ");
		filterChain.doFilter(request, response);
	}

}