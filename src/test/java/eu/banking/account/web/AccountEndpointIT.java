package eu.banking.account.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.banking.account.TestData;
import eu.banking.account.api.CreditData;
import eu.banking.account.api.DebitData;
import eu.banking.account.api.MoneyAmountData;
import eu.banking.account.api.MoneyExchangeData;
import eu.banking.account.model.Account;
import eu.banking.account.model.Currencies;
import eu.banking.account.model.Customer;
import eu.banking.account.repository.AccountBalanceRepository;
import eu.banking.account.repository.AccountRepository;
import eu.banking.account.repository.CustomerRepository;
import eu.banking.account.repository.TestMysqlContainer;
import eu.banking.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.hamcrest.CoreMatchers.is;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@SpringBootTest
@ExtendWith(SpringExtension.class)
@Testcontainers
@TestPropertySource(value = {
        "classpath:application-test.properties"
})
@AutoConfigureMockMvc
public class AccountEndpointIT {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private  AccountBalanceRepository accountBalanceRepository;
    @Autowired
    private  AccountService accountService;
    @Autowired
    private  MockMvc mvc;
    @Autowired
    private  ObjectMapper objectMapper;

    @Container
    public static final TestMysqlContainer myDb = TestMysqlContainer.getInstance();

    @BeforeEach
    public void init() {
        prepareTestData();
    }
    @Test
    public void testQueryBalance() throws Exception {
                mvc.perform(MockMvcRequestBuilders
                        .get("/api/v1.0/account/" + TestData.IBAN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].currency", is(Currencies.EUR.name())))
                        .andExpect(jsonPath("$[0].amount", comparesEqualTo(Double.valueOf(100.0))));
    }
    @Test
    @Rollback
    public void testAddCredit() throws Exception {

        List<MoneyAmountData> amounts = new ArrayList<>();
        amounts.add(new MoneyAmountData()
                .setCurrency(Currencies.EUR)
                .setAmount(BigDecimal.valueOf(10L)));

        amounts.add(new MoneyAmountData()
                .setCurrency(Currencies.GBP)
                .setAmount(BigDecimal.valueOf(20L)));

        CreditData credit = CreditData.builder()
                        .iban(TestData.IBAN)
                .amounts(amounts).build();

        String body = objectMapper.writeValueAsString(credit);

        mvc.perform(post("/api/v1.0/account/credit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        List<MoneyAmountData> balances = accountService.findAccountBalance(TestData.IBAN);

        BigDecimal balanceEUR = balances.stream().filter(ab -> ab.getCurrency() == Currencies.EUR)
                        .map(MoneyAmountData::getAmount).findFirst().get();
        assertThat(balanceEUR.compareTo(BigDecimal.valueOf(110)));

        BigDecimal balanceGBP = balances.stream().filter(ab -> ab.getCurrency() == Currencies.GBP)
                .map(MoneyAmountData::getAmount).findFirst().get();

        assertThat(balanceGBP.compareTo(BigDecimal.valueOf(30)));
    }

    @Test
    @Rollback
    public void testDebit() throws Exception {

        DebitData debit = DebitData.builder()
                .iban(TestData.IBAN)
                .money(new MoneyAmountData()
                        .setAmount(BigDecimal.valueOf(20))
                        .setCurrency(Currencies.EUR))
                .build();

        String body = objectMapper.writeValueAsString(debit);

        mvc.perform(post("/api/v1.0/account/debit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        List<MoneyAmountData> balances = accountService.findAccountBalance(TestData.IBAN);
        BigDecimal balanceEUR = balances.stream().filter(ab -> ab.getCurrency() == Currencies.EUR)
                .map(MoneyAmountData::getAmount).findFirst().get();
        assertThat(balanceEUR.compareTo(BigDecimal.valueOf(80)));
    }

    @Test
    @Rollback
    public void testOutOfFunds() throws Exception {

        DebitData debit = DebitData.builder()
                .iban(TestData.IBAN)
                .money(new MoneyAmountData()
                        .setAmount(BigDecimal.valueOf(50))
                        .setCurrency(Currencies.GBP))
                .build();

        String body = objectMapper.writeValueAsString(debit);
        mvc.perform(post("/api/v1.0/account/debit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$[0].message", startsWith("Not enough")));
     }

    @Test
    @Rollback
    public void testExchange() throws Exception {
        MoneyExchangeData exhangeData = MoneyExchangeData.builder()
                .iban(TestData.IBAN)
                .sourceCurrency(Currencies.EUR)
                .targetCurrency(Currencies.GBP)
                .targetAmount(BigDecimal.valueOf(50))
                .build();

        String body = objectMapper.writeValueAsString(exhangeData);

        mvc.perform(post("/api/v1.0/account/exchange")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        List<MoneyAmountData> balances = accountService.findAccountBalance(TestData.IBAN);
        BigDecimal balanceEUR = balances.stream().filter(ab -> ab.getCurrency() == Currencies.EUR)
                .map(MoneyAmountData::getAmount).findFirst().get();
        assertThat(balanceEUR.compareTo(BigDecimal.valueOf(40.43)));

        BigDecimal balanceGBP = balances.stream().filter(ab -> ab.getCurrency() == Currencies.GBP)
                .map(MoneyAmountData::getAmount).findFirst().get();
        assertThat(balanceGBP.compareTo(BigDecimal.valueOf(60)));
    }

    private void prepareTestData() {
        Account account = TestData.createAccountData();
        Customer savedCustomer = customerRepository.save(account.getCustomer());
        account.setCustomer(savedCustomer);
        Account savedAccount = accountRepository.save(account);
        account.getAccountBalances().stream()
                .forEach(accountBalance -> {
                    accountBalance.setAccount(savedAccount);
                    accountBalanceRepository.save(accountBalance);
                });
    }

}
