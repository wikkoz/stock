package com.domain.services;

import com.domain.model.Rate;
import com.domain.model.Trend;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

public class TrendCalculator {
    public Trend getTrend(String currency, List<Rate> rates) {
        rates.sort(Comparator.comparing(Rate::getEffectiveDate));

        BigDecimal first = rates.get(0).getValue();
        BigDecimal last = rates.get(rates.size()-1).getValue();

        return new Trend(currency, last.subtract(first).multiply(new BigDecimal(100)).divide(first, 4, RoundingMode.UP));
    }
}
