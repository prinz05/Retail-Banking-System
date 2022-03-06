package com.mfpe.account.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.mfpe.account.model.AccountType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class Account {

	/**
	 *  Account Entity Class persisted in Repository
	 */
	@Id
	@Column
	private String accountId;
	
	private String customerId;

	@Setter
	private double balance;

	private AccountType accountType;

}