package eu.banking.account.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Entity
@Table
public class Customer extends ABaseEntity {
    @Column(nullable = false)
    private String name;
}
