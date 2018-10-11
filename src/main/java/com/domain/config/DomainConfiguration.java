package com.domain.config;

import com.database.repository.RatesRepository;
import com.domain.api.RatesService;
import com.domain.services.LinearRegressionService;
import com.domain.services.TrendCalculator;
import com.domain.services.WebRatesRepository;
import com.rest.api.NbpWebclient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfiguration {

    @Bean
    RatesService ratesService(RatesRepository ratesRepository, NbpWebclient nbpWebclient) {
        LinearRegressionService linearRegressionService = new LinearRegressionService();
        TrendCalculator trendCalculator = new TrendCalculator();
        WebRatesRepository webRatesRepository = new WebRatesRepository(nbpWebclient);
        return new RatesService(ratesRepository, webRatesRepository, trendCalculator, linearRegressionService);
    }
}
