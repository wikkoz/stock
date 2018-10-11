package com.database.repository;

import com.database.entities.DateRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.Set;

@Repository
interface JpaSimpleRatesRepository extends JpaRepository<DateRateEntity, Long> {
    Set<DateRateEntity> findAllByDateBetweenAndCurrency(LocalDate startDate, LocalDate endDate, String currency);

    @Query("SELECT distinct currency FROM DateRateEntity")
    Set<String> findAllCurrencies();
}
