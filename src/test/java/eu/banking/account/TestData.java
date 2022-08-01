package eu.banking.account;

import eu.banking.account.model.Account;
import eu.banking.account.model.AccountBalance;
import eu.banking.account.model.Currencies;
import eu.banking.account.model.Customer;

import java.math.BigDecimal;

public class TestData {

    public static final String IBAN = "EE12345678901";

    public static Account createAccountData() {

        Customer customer = new Customer()
                .setName("Juhan");

        Account account = new Account()
                .setIban(IBAN)
                .setCustomer(customer);

        account.getAccountBalances().add(new AccountBalance()
                .setAccount(account)
                .setCurrency(Currencies.EUR)
                .setAmount(new BigDecimal(100)));

        account.getAccountBalances().add(new AccountBalance()
                .setAccount(account)
                .setCurrency(Currencies.GBP)
                .setAmount(new BigDecimal(10)));
        return account;
    }
}
