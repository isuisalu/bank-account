package eu.banking.account.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;

import java.math.BigDecimal;

import static javax.persistence.EnumType.STRING;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Entity
@Table(name = "account_balance")
public class AccountBalance extends ABaseEntity {

    @ManyToOne
    @JoinColumn(name="account_id", referencedColumnName="id", nullable = false,
            foreignKey=@ForeignKey(name = "fk_balance_account"))
    private Account account;

    @Enumerated(STRING)
    @Column(nullable = false)
    private Currencies currency;
    @Column(nullable = false)
    private BigDecimal amount;
}
