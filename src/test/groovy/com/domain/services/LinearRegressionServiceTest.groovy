package com.domain.services

import com.domain.model.Rate
import spock.lang.Specification

import java.time.LocalDate

class LinearRegressionServiceTest extends Specification {
    LinearRegressionService linearRegressionService = new LinearRegressionService()

    def "should set same value for 1 and 7 days in advance if all rates all the same"() {
        given:
        List<Rate> rates = [new Rate("USD", BigDecimal.ONE, LocalDate.now().minusDays(6)),
                            new Rate("USD", BigDecimal.ONE, LocalDate.now().minusDays(4)),
                            new Rate("USD", BigDecimal.ONE, LocalDate.now().minusDays(2)),
                            new Rate("USD", BigDecimal.ONE, LocalDate.now())]

        List<Integer> daysAdvance = [1,7]

        when:
        List<Rate> prediction = linearRegressionService.predictRates(rates, daysAdvance)

        then:
        prediction.findAll {rate -> rate.value == BigDecimal.ONE}.size() == 2;
    }

    def "should set proportional value for 1 and 7 days in advance"() {
        given:
        List<Rate> rates = [new Rate("USD", new BigDecimal(114), LocalDate.now().minusDays(14)),
                            new Rate("USD", new BigDecimal(107), LocalDate.now().minusDays(7)),
                            new Rate("USD", new BigDecimal(100), LocalDate.now())]

        List<Integer> daysAdvance = [1,7]

        when:
        List<Rate> prediction = linearRegressionService.predictRates(rates, daysAdvance)

        then:
        prediction.find() {rate -> rate.value == new BigDecimal(99)} != null;
        prediction.find() {rate -> rate.value == new BigDecimal(93)} != null;
    }
}
