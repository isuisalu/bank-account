package eu.banking.account.api;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class AccountData {
    @NotNull
    private String iban;
    private CustomerData customer;
    private List<MoneyAmountData> balances;
}
