package com.domain.api;

import com.database.repository.RatesRepository;
import com.domain.model.Rate;
import com.domain.model.Trend;
import com.domain.services.WebRatesRepository;
import com.domain.services.TrendCalculator;
import com.domain.services.LinearRegressionService;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
public class RatesService {
    private final RatesRepository ratesRepository;
    private final WebRatesRepository webRatesRepository;
    private final TrendCalculator trendCalculator;
    private final LinearRegressionService linearRegression;

    public List<Rate> getRates(String currency, LocalDate inclusiveStartDate, LocalDate inclusiveEndDate) {
        Flux<Rate> savedRates = ratesRepository.findRates(currency, inclusiveStartDate, inclusiveEndDate);
        Flux<Rate> newRates = webRatesRepository.updateMissingRates(savedRates, inclusiveStartDate, inclusiveEndDate);
        ratesRepository.save(Flux.concat(newRates));

        return savedRates.concatWith(findRatesWithCurrency(newRates, currency)).collectList().block();
    }

    public List<String> getCurrencies() {
        return ratesRepository.getAllCurrencies().collectList().block();
    }

    public Trend getTrend(String currency, LocalDate inclusiveStartDate, LocalDate inclusiveEndDate) {
        return trendCalculator.getTrend(currency, getRates(currency, inclusiveStartDate, inclusiveEndDate));
    }

    public List<Rate> getPredictions(String currency) {
        LocalDate today = LocalDate.now();
        LocalDate prevDate = today.minusDays(30);

        List<Rate> rates = getRates(currency, prevDate, today);
        return linearRegression.predictRates(rates, Lists.newArrayList(1, 7));
    }

    private Flux<Rate> findRatesWithCurrency(Flux<Rate> rates, String currency) {
        return rates.filter(rate -> currency.equals(rate.getCurrency()));
    }


}