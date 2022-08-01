package eu.banking.account.web;

import eu.banking.account.api.CreditData;
import eu.banking.account.api.MoneyAmountData;
import eu.banking.account.api.DebitData;
import eu.banking.account.api.MoneyExchangeData;
import eu.banking.account.service.AccountService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@OpenAPIDefinition(info = @Info(title = "Bank Account API", version = "1.0"),
        servers = @Server(url = "${swagger.server.url}", description = "Account API operation(s)"),
        tags = @Tag(name = "Account API operation(s)"))

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1.0/account")
public class AccountController {

    private static final String ACCOUNTS_TAG = "Account API operation(s)";

    private final AccountService accountService;

    @Operation(description = "Makes credit transfer on bank account", tags = {ACCOUNTS_TAG})
    @PostMapping("/credit")
    public ResponseEntity<Void> credit(@Parameter(description = "PIM item nr") @Valid @RequestBody CreditData creditData) {
        accountService.credit(creditData);
        return ResponseEntity.ok().build();
    }
    @Operation(description = "Makes debit transfer on bank account", tags = {ACCOUNTS_TAG})
    @PostMapping("/debit")
    public ResponseEntity<Void> debit(@Valid @RequestBody DebitData debitData) {
        accountService.debit(debitData);
        return ResponseEntity.ok().build();
    }
    @Operation(description = "Makes currency exchange transfer on bank account", tags = {ACCOUNTS_TAG})
    @PostMapping("/exchange")
    public ResponseEntity<Void> exchange(@Valid @RequestBody MoneyExchangeData exchangeData) {
        accountService.exchange(exchangeData);
        return ResponseEntity.ok().build();
    }

    @Operation(description = "Fetches bank account balances", tags = {ACCOUNTS_TAG})
    @GetMapping("/{iban}")
    public List<MoneyAmountData> getBalance(@PathVariable String iban) {
        return accountService.findAccountBalance(iban);
    }
}
