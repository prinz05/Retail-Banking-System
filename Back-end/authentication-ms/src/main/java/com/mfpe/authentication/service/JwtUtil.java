package com.mfpe.authentication.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtUtil {

	/**
	 * Service Layer Class for Authentication Microservice
	 */

	private String secretkey = "${jwt.secret}";

	/**
	 * @param token
	 * @return String
	 */
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	/**
	 * @param token
	 * @param claimsResolver
	 */
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	/**
	 * @param token
	 * @return Claims
	 */
	private Claims extractAllClaims(String token) {
		return Jwts.parser().setSigningKey(secretkey).parseClaimsJws(token).getBody();
	}

	/**
	 * @param userDetails
	 * @return String whose value is token
	 */
	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		return createToken(claims, userDetails.getUsername());
	}

	/**
	 * @param claims
	 * @param subject
	 * @return String whose value is token
	 */
	private String createToken(Map<String, Object> claims, String subject) {
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
				.signWith(SignatureAlgorithm.HS256, secretkey).compact();
	}

	/**
	 * @param token
	 * @return Boolean value that tells the validity of the token
	 */
	public Boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(secretkey).parseClaimsJws(token).getBody();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}