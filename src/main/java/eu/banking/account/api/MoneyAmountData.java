package eu.banking.account.api;

import eu.banking.account.model.Currencies;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@NoArgsConstructor
@Accessors(chain = true)
@Data
public class MoneyAmountData {
    @NotNull
    private Currencies currency;
    @NotNull
    @Digits(integer = 19, fraction = 2)
    private BigDecimal amount;

}
