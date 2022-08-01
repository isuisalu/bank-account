package eu.banking.account.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Entity
@Table
public class Account extends ABaseEntity {

    @Column(nullable = false)
    private String iban;

    @ManyToOne
    @JoinColumn(name="customer_id", referencedColumnName="id", nullable = false,
            foreignKey=@ForeignKey(name = "fk_account_customer"))
    private Customer customer;

    @OneToMany(mappedBy = "account")
    private List<AccountBalance> accountBalances = new ArrayList<>();

    @Transient
    public Optional<AccountBalance> getBalance(Currencies currency) {
        return accountBalances.stream().filter(ab -> ab.getCurrency() == currency)
                .findFirst();
    }
}
