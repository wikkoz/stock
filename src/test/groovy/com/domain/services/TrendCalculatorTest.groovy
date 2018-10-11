package com.domain.services

import com.domain.model.Rate
import com.domain.model.Trend
import spock.lang.Specification

import java.time.LocalDate

class TrendCalculatorTest extends Specification {
    TrendCalculator trendCalculator = new TrendCalculator()

    def "should correctly count trend for currency"() {
        when:
        Trend trend = trendCalculator.getTrend("USD", rates)

        then:
        trend.trend == value;

        where:
        rates                                                                                                                                                        | value
        [new Rate("USD", BigDecimal.ONE, LocalDate.now().minusDays(6)), new Rate("USD", BigDecimal.ONE, LocalDate.now())]               | BigDecimal.ZERO
        [new Rate("USD", new BigDecimal(100), LocalDate.now().minusDays(6)), new Rate("USD", BigDecimal.ONE, LocalDate.now())]      | new BigDecimal(-99)
    }
}
