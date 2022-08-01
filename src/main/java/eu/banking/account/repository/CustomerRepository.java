package eu.banking.account.repository;

import eu.banking.account.model.Account;
import eu.banking.account.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Account> {
}
