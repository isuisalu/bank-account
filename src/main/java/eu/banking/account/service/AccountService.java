package eu.banking.account.service;

import eu.banking.account.api.CreditData;
import eu.banking.account.api.DebitData;
import eu.banking.account.api.MoneyAmountData;
import eu.banking.account.api.MoneyExchangeData;
import eu.banking.account.model.Account;
import eu.banking.account.model.AccountBalance;
import eu.banking.account.model.ExchangeRate;
import eu.banking.account.repository.AccountBalanceRepository;
import eu.banking.account.repository.AccountRepository;
import eu.banking.account.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Transactional
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountBalanceRepository accountBalanceRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final ModelMapper mapper;

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${try.external.url}")
    private String tryExternalUrl;

    public void credit(CreditData creditData) {
        accountRepository.findByIban(creditData.getIban()).ifPresentOrElse(acc -> {
                    creditData.getAmounts().stream().forEach(amount -> {
                        Optional<AccountBalance> balanceOpt = acc.getBalance(amount.getCurrency());
                        AccountBalance currentBalance = null;

                        if (balanceOpt.isEmpty()) {
                            currentBalance = new AccountBalance()
                                    .setAmount(amount.getAmount())
                                    .setCurrency(amount.getCurrency())
                                    .setAccount(acc);
                        } else {
                            currentBalance = balanceOpt.get();
                            currentBalance.setAmount(currentBalance.getAmount()
                                    .add(amount.getAmount()));
                        }
                        accountBalanceRepository.saveAndFlush(currentBalance);
                    });
                }, () -> {
                    String msg = String.format("Couldn't find account with iban %s",
                            creditData.getIban());
                    log.error(msg);
                    throw new IllegalArgumentException(msg);
                }
        );
    }

    public void debit(DebitData debitData) {
        accountRepository.findByIban(debitData.getIban()).ifPresentOrElse(acc -> {
                    Optional<AccountBalance> balanceOpt = acc
                            .getBalance(debitData.getMoney().getCurrency());
                    if (balanceOpt.isEmpty() ||
                            (balanceOpt.isPresent() && balanceOpt.get()
                                    .getAmount().compareTo(debitData.getMoney().getAmount()) < 0)) {
                        String msg = String.format("Not enough money on account with iban %s",
                                debitData.getIban());
                        log.error(msg);
                        throw new IllegalArgumentException(msg);
                    } else {
                        try {
                            ResponseEntity<String> response = restTemplate.getForEntity(tryExternalUrl,
                                    String.class);
                            response.getStatusCode()
                        } catch(Exception e) {
                            log.error(e.getMessage(), e);
                        }
                        AccountBalance currentBalance = balanceOpt.get();
                        currentBalance.setAmount(currentBalance.getAmount()
                                .subtract(debitData.getMoney().getAmount()));
                    }
                }, () -> {
                    String msg = String.format("Couldn't find account with iban %s",
                            debitData.getIban());
                    log.error(msg);
                    throw new IllegalArgumentException(msg);
                }
        );
    }

    public void exchange(MoneyExchangeData exchangeData) {
        Optional<ExchangeRate> exchangeRateOpt = exchangeRateRepository
                .findBySourceCurrencyAndTargetCurrency(exchangeData.getSourceCurrency(),
                        exchangeData.getTargetCurrency());
        if (exchangeRateOpt.isPresent()) {
            Optional<Account> accountOpt = accountRepository.findByIban(exchangeData.getIban());
            if (accountOpt.isPresent()) {
                Optional<AccountBalance> balanceOpt = accountOpt.get()
                        .getBalance(exchangeData.getSourceCurrency());
                BigDecimal srcAmount = exchangeData.getTargetAmount()
                        .divide(exchangeRateOpt.get().getRate(), 2, RoundingMode.HALF_UP);
                if (balanceOpt.isEmpty() || (balanceOpt.isPresent() &&
                        balanceOpt.get().getAmount().compareTo(srcAmount) <= 0)) {
                    String msg = String.format("Not enough money for conversion: %s",
                            exchangeData);
                    throw new IllegalArgumentException(msg);
                }
                DebitData debitData = DebitData.builder()
                        .iban(exchangeData.getIban())
                        .money(new MoneyAmountData()
                                .setCurrency(exchangeData.getSourceCurrency())
                                .setAmount(srcAmount))
                        .build();
                CreditData creditData = CreditData.builder()
                                .iban(exchangeData.getIban())
                        .amounts(List.of(new MoneyAmountData()
                                .setCurrency(exchangeData.getTargetCurrency())
                                .setAmount(exchangeData.getTargetAmount())))
                                .build();

                debit(debitData);
                credit(creditData);
            } else {
                String msg = String.format("Couldn't find account for IBAN %s",
                        exchangeData.getIban());
                log.error(msg);
                throw new IllegalArgumentException(msg);
            }
        } else {
            String msg = String.format("Couldn't find exchange rate between %s -> %s",
                    exchangeData.getSourceCurrency(), exchangeData.getTargetCurrency());
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }
    public List<MoneyAmountData> findAccountBalance(String iban) {
        Optional<Account> accountOpt = accountRepository.findByIban(iban);
        if (accountOpt.isPresent()) {
            return accountOpt.get().getAccountBalances()
                    .stream().map(balance -> mapper.map(balance, MoneyAmountData.class))
                    .collect(Collectors.toList());
        } else {
            String msg = String.format("Couldn't find account with iban %s",
                    iban);
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }
}
