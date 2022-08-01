package eu.banking.account.api;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DebitData {
    private String iban;
    private MoneyAmountData money;
}
