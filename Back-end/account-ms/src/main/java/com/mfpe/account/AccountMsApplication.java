package com.mfpe.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableFeignClients
@Slf4j
public class AccountMsApplication {

	/**
	 * Main Application for Account Microservice
	 */
	public static void main(String[] args) {
		
		SpringApplication.run(AccountMsApplication.class, args);
		log.info("Account Main Application Started");
		
	}

}
