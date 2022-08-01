package eu.banking.account.repository;

import eu.banking.account.model.Account;
import eu.banking.account.model.AccountBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AccountBalanceRepository extends JpaRepository<AccountBalance, Long>, JpaSpecificationExecutor<Account> {
}
