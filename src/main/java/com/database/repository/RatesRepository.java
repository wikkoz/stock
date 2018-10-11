package com.database.repository;

import com.database.JpaRatesMapper;
import com.database.entities.DateRateEntity;
import com.domain.model.Rate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class RatesRepository {
    private final JpaSimpleRatesRepository jpaSimpleRatesRepository;

    public Flux<Rate> findRates(String currency, LocalDate startDate, LocalDate endDate) {
        return Flux.fromIterable(jpaSimpleRatesRepository.findAllByDateBetweenAndCurrency(startDate.minusDays(1), endDate, currency))
                .map(JpaRatesMapper::map);
    }

    public Flux<String> getAllCurrencies() {
        return Flux.fromIterable(jpaSimpleRatesRepository.findAllCurrencies());
    }

    @Transactional
    public Flux<Rate> save(Flux<Rate> rates) {
        List<DateRateEntity> entities = rates.map(JpaRatesMapper::map)
                .collectList()
                .block();
        return Flux.fromIterable(jpaSimpleRatesRepository.saveAll(entities))
                .map(JpaRatesMapper::map);
    }
}
