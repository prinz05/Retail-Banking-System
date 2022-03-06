package com.mfpe.customer.dto;

import java.util.Date;
import java.util.List;

import com.mfpe.customer.model.Gender;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CustomerDTO {

	/**
	 * CustomerDTO Dto for transferring the information
	 */
	private String customerId;
	private String customerName;
	private String address;
	private Date dateOfBirth;
	private String panNo;
	private Gender gender;
	private List<AccountDto> accounts;

}
