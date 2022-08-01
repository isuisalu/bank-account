package eu.banking.account.repository;

import eu.banking.account.model.Currencies;
import eu.banking.account.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ExchangeRateRepository  extends JpaRepository<ExchangeRate, Long>, JpaSpecificationExecutor<ExchangeRate> {

    @Query("select er from ExchangeRate er where er.sourceCurrency = :sourceCurrency and" +
            " er.targetCurrency = :targetCurrency")
    Optional<ExchangeRate> findBySourceCurrencyAndTargetCurrency(@Param("sourceCurrency") Currencies sourceCurrency,
                                                                 @Param("targetCurrency")  Currencies targettCurrency);
}
