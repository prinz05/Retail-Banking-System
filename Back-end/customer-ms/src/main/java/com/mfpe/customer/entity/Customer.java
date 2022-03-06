package com.mfpe.customer.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mfpe.customer.model.Gender;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Customer {

	/**
	 * Customer Entity persisted in Repository
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "slno")
	private long id;

	private String customerId;

	@NotBlank
	@Size(min = 3, max = 20, message = "Customer Name should be in between 3 to 20 characters")
	private String customerName;

	@NotBlank(message = "Address should not be blank")
	@Size(max = 50)
	private String address;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date dateOfBirth;

	@NotBlank(message = "PAN number should not be blank")
	@Pattern(regexp = "[A-Z]{5}[0-9]{4}[A-Z]{1}", message = "Pan No should contain 5 Capital letters, 4 digits, 1 Capital letter")
	private String panNo;

	@NotNull
	private Gender gender;

	@NotNull
	@Size(min = 6, message = "Password must be of atleast 6 characters")
	private String password;

}
