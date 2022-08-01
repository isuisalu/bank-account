package eu.banking.account.api;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class CreditData {
    private String iban;
    private List<MoneyAmountData> amounts;
}
