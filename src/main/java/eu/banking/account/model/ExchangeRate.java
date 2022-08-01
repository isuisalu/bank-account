package eu.banking.account.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.math.BigDecimal;

import static javax.persistence.EnumType.STRING;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Entity
@Table(name = "exchange_rate")
public class ExchangeRate extends ABaseEntity {
    @Enumerated(STRING)
    @Column(name = "source_currency", nullable = false)
    private Currencies sourceCurrency;
    @Enumerated(STRING)
    @Column(name = "target_currency", nullable = false)
    private Currencies targetCurrency;
    @Column(nullable = false)
    private BigDecimal rate;
}
