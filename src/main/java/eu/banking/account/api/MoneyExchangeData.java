package eu.banking.account.api;

import eu.banking.account.model.Currencies;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Builder
@Data
public class MoneyExchangeData {
    @NotNull
    private String iban;
    @NotNull
    private Currencies sourceCurrency;
    @NotNull
    private Currencies targetCurrency;
    @NotNull
    private BigDecimal targetAmount;
}
