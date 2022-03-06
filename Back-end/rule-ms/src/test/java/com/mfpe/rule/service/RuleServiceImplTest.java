package com.mfpe.rule.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mfpe.rule.dto.RuleStatus;
import com.mfpe.rule.dto.ServiceCharge;
import com.mfpe.rule.exception.TokenNotFoundException;
import com.mfpe.rule.feign.AccountFeign;
import com.mfpe.rule.model.AccountDto;

@SpringBootTest
class RuleServiceImplTest {

	@Mock
	private AccountFeign accountClient;

	@InjectMocks
	private RuleServiceImpl ruleService;

	private AccountDto savingsAccountDto;
	private AccountDto currentAccountDto;
	private String customerId;
	private String token;
	private List<AccountDto> accountList;
	private List<ServiceCharge> serviceChargeList;
	private RuleStatus completelyOkStatus;
	private RuleStatus partiallyOkStatus;
	private RuleStatus notOkStatus;

	@BeforeEach
	void init() {
		MockitoAnnotations.openMocks(this);
	}

	@BeforeEach
	void setUp() {

		savingsAccountDto = new AccountDto("SBACS000001", 8000.0);
		currentAccountDto = new AccountDto("SBACC000001", 15000.0);
		customerId = "SBCU000001";
		token = "token";
		accountList = new ArrayList<>();
		serviceChargeList = new ArrayList<>();
		completelyOkStatus = new RuleStatus("Allowed", "Completely OK");
		partiallyOkStatus = new RuleStatus("Allowed", "Partially OK");
		notOkStatus = new RuleStatus("Denied", "Not OK");

	}

	@AfterEach
	void tearDown() {

		savingsAccountDto = null;
		currentAccountDto = null;
		customerId = null;
		token = null;
		accountList = null;
		serviceChargeList = null;
		completelyOkStatus = null;
		partiallyOkStatus = null;
		notOkStatus = null;

	}

	@Test
	void testInvalidEvaluateMinBal() {

		Assertions.assertThrows(TokenNotFoundException.class,
				() -> this.ruleService.evaluateMinBal(1000.0, "SBACS000001", null));

	}

	@Test
	void testEvaluateMinBalSavingsClosingBalanceGreaterThan5000() {

		when(accountClient.getAccount("token", "SBACS000001"))
				.thenReturn(new ResponseEntity<AccountDto>(savingsAccountDto, HttpStatus.OK));

		RuleStatus resultStatus = ruleService.evaluateMinBal(1000, "SBACS000001", "token");
		assertThat(completelyOkStatus.getMessage()).isEqualTo(resultStatus.getMessage());
		assertThat(completelyOkStatus.getStatus()).isEqualTo(resultStatus.getStatus());
	}

	@Test
	void testEvaluateMinBalSavingsClosingBalanceGreaterThanEqualto5000() {

		when(accountClient.getAccount("token", "SBACS000001"))
				.thenReturn(new ResponseEntity<AccountDto>(savingsAccountDto, HttpStatus.OK));

		RuleStatus resultStatus = ruleService.evaluateMinBal(3000, "SBACS000001", "token");
		assertThat(completelyOkStatus.getMessage()).isEqualTo(resultStatus.getMessage());
		assertThat(completelyOkStatus.getStatus()).isEqualTo(resultStatus.getStatus());
	}

	@Test
	void testEvaluateMinBalSavingsClosingBalanceLessThan5000() {
		when(accountClient.getAccount("token", "SBACS000001"))
				.thenReturn(new ResponseEntity<AccountDto>(savingsAccountDto, HttpStatus.OK));

		RuleStatus resultStatus = ruleService.evaluateMinBal(4000, "SBACS000001", "token");
		assertThat(partiallyOkStatus.getMessage()).isEqualTo(resultStatus.getMessage());
		assertThat(partiallyOkStatus.getStatus()).isEqualTo(resultStatus.getStatus());

	}

	@Test
	void testEvaluateMinBalSavingsClosingBalanceEqualtoZero() {
		when(accountClient.getAccount("token", "SBACS000001"))
				.thenReturn(new ResponseEntity<AccountDto>(savingsAccountDto, HttpStatus.OK));

		RuleStatus resultStatus = ruleService.evaluateMinBal(8000, "SBACS000001", "token");
		assertThat(partiallyOkStatus.getMessage()).isEqualTo(resultStatus.getMessage());
		assertThat(partiallyOkStatus.getStatus()).isEqualTo(resultStatus.getStatus());

	}

	@Test
	void testEvaluateMinBalSavingsClosingBalanceLessThan0() {
		when(accountClient.getAccount("token", "SBACS000001"))
				.thenReturn(new ResponseEntity<AccountDto>(savingsAccountDto, HttpStatus.OK));

		RuleStatus resultStatus = ruleService.evaluateMinBal(9000, "SBACS000001", "token");
		assertThat(notOkStatus.getMessage()).isEqualTo(resultStatus.getMessage());
		assertThat(notOkStatus.getStatus()).isEqualTo(resultStatus.getStatus());

	}

	@Test
	void testEvaluateMinBalCurrentClosingBalanceGreaterThan11000() {

		when(accountClient.getAccount("token", "SBACC000001"))
				.thenReturn(new ResponseEntity<AccountDto>(currentAccountDto, HttpStatus.OK));

		RuleStatus resultStatus = ruleService.evaluateMinBal(2000, "SBACC000001", "token");
		assertThat(completelyOkStatus.getMessage()).isEqualTo(resultStatus.getMessage());
		assertThat(completelyOkStatus.getStatus()).isEqualTo(resultStatus.getStatus());
	}

	@Test
	void testEvaluateMinBalCurrentClosingBalanceEqualto11000() {

		when(accountClient.getAccount("token", "SBACC000001"))
				.thenReturn(new ResponseEntity<AccountDto>(currentAccountDto, HttpStatus.OK));

		RuleStatus resultStatus = ruleService.evaluateMinBal(4000, "SBACC000001", "token");
		assertThat(completelyOkStatus.getMessage()).isEqualTo(resultStatus.getMessage());
		assertThat(completelyOkStatus.getStatus()).isEqualTo(resultStatus.getStatus());
	}

	@Test
	void testEvaluateMinBalCurrentClosingBalanceLessThan11000() {

		when(accountClient.getAccount("token", "SBACC000001"))
				.thenReturn(new ResponseEntity<AccountDto>(currentAccountDto, HttpStatus.OK));

		RuleStatus resultStatus = ruleService.evaluateMinBal(5000, "SBACC000001", "token");
		assertThat(partiallyOkStatus.getMessage()).isEqualTo(resultStatus.getMessage());
		assertThat(partiallyOkStatus.getStatus()).isEqualTo(resultStatus.getStatus());
	}

	@Test
	void testEvaluateMinBalCurrentClosingBalanceEqualtoZero() {

		when(accountClient.getAccount("token", "SBACC000001"))
				.thenReturn(new ResponseEntity<AccountDto>(currentAccountDto, HttpStatus.OK));

		RuleStatus resultStatus = ruleService.evaluateMinBal(15000, "SBACC000001", "token");
		assertThat(partiallyOkStatus.getMessage()).isEqualTo(resultStatus.getMessage());
		assertThat(partiallyOkStatus.getStatus()).isEqualTo(resultStatus.getStatus());
	}

	@Test
	void testEvaluateMinBalCurrentClosingBalanceLessThanZero() {

		when(accountClient.getAccount("token", "SBACC000001"))
				.thenReturn(new ResponseEntity<AccountDto>(currentAccountDto, HttpStatus.OK));

		RuleStatus resultStatus = ruleService.evaluateMinBal(20000, "SBACC000001", "token");
		assertThat(notOkStatus.getMessage()).isEqualTo(resultStatus.getMessage());
		assertThat(notOkStatus.getStatus()).isEqualTo(resultStatus.getStatus());
	}

	@Test
	void testInvalidGetServiceCharges() {

		Assertions.assertThrows(TokenNotFoundException.class,
				() -> this.ruleService.getServiceCharges("SBCU000001", null));

	}

	@Test
	void testGetServiceChargesBalanceLessThan5000And11000() {

		accountList.add(new AccountDto("SBACS000001", 200));
		accountList.add(new AccountDto("SBACC000001", 500));

		when(accountClient.getCustomerAccounts(customerId))
				.thenReturn(new ResponseEntity<List<AccountDto>>(accountList, HttpStatus.OK));

		serviceChargeList.add(new ServiceCharge("SBACS000001",
				"Your Savings Account is not satisfying the minimum balance criteria, ₹200 will be detucted.", 200));
		serviceChargeList.add(new ServiceCharge("SBACC000001",
				"Your Current Account is not satisfying the minimum balance criteria, ₹800 will be detucted.", 500));

		List<ServiceCharge> result = ruleService.getServiceCharges(customerId, token);

		assertThat(serviceChargeList.get(0).getAccountId()).isEqualTo(result.get(0).getAccountId());

	}

	@Test
	void testGetServiceChargesBalanceLessThan5000AndGreaterThan11000() {

		accountList.add(new AccountDto("SBACS000001", 200));
		accountList.add(new AccountDto("SBACC000001", 12000));

		when(accountClient.getCustomerAccounts(customerId))
				.thenReturn(new ResponseEntity<List<AccountDto>>(accountList, HttpStatus.OK));

		serviceChargeList.add(new ServiceCharge("SBACS000001",
				"Your Savings Account is not satisfying the minimum balance criteria, ₹200 will be detucted.", 200));
		serviceChargeList.add(new ServiceCharge("SBACC000001", "maintaining minimum amount, no detection.", 12000));

		List<ServiceCharge> result = ruleService.getServiceCharges(customerId, token);

		assertThat(serviceChargeList.get(0).getAccountId()).isEqualTo(result.get(0).getAccountId());

	}

	@Test
	void testGetServiceChargesBalanceGreaterThan5000AndLessThan1100() {

		accountList.add(new AccountDto("SBACS000001", 12000));
		accountList.add(new AccountDto("SBACC000001", 500));

		when(accountClient.getCustomerAccounts(customerId))
				.thenReturn(new ResponseEntity<List<AccountDto>>(accountList, HttpStatus.OK));

		serviceChargeList.add(new ServiceCharge("SBACS000001", "maintaining minimum amount, no detection.", 12000));
		serviceChargeList.add(new ServiceCharge("SBACC000001",
				"Your Current Account is not satisfying the minimum balance criteria, ₹800 will be detucted.", 500));

		List<ServiceCharge> result = ruleService.getServiceCharges(customerId, token);

		assertThat(serviceChargeList.get(0).getAccountId()).isEqualTo(result.get(0).getAccountId());
	}

	@Test
	void testGetServiceChargesBalanceGreaterThan5000And1100() {

		accountList.add(new AccountDto("SBACS000001", 12000));
		accountList.add(new AccountDto("SBACC000001", 15000));

		when(accountClient.getCustomerAccounts(customerId))
				.thenReturn(new ResponseEntity<List<AccountDto>>(accountList, HttpStatus.OK));

		serviceChargeList.add(new ServiceCharge("SBACS000001", "maintaining minimum amount, no detection.", 12000));
		serviceChargeList.add(new ServiceCharge("SBACC000001", "maintaining minimum amount, no detection.", 15000));

		List<ServiceCharge> result = ruleService.getServiceCharges(customerId, token);

		assertThat(serviceChargeList.get(0).getAccountId()).isEqualTo(result.get(0).getAccountId());
	}

}