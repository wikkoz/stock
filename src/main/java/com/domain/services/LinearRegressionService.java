package com.domain.services;

import com.domain.model.Rate;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

public class LinearRegressionService {
    public List<Rate> predictRates(List<Rate> data, List<Integer> datesShift) {
        LocalDate today = LocalDate.now();
        String currency = data.stream()
                .map(Rate::getCurrency)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Given data should not be null"));

        SimpleRegression simpleRegression = makeRegression(data);

        return datesShift.stream()
                .map(i -> new Rate(currency, BigDecimal.valueOf(simpleRegression.predict(i)), today.plusDays(i)))
                .collect(Collectors.toList());
    }

    private SimpleRegression makeRegression(List<Rate> data) {
        SimpleRegression simpleRegression = new SimpleRegression(true);
        LocalDate today = LocalDate.now();

        double[][] dataArray = data.stream()
                .map(rate -> new double[]{(double) DAYS.between(today, rate.getEffectiveDate()), rate.getValue().doubleValue()})
                .toArray(double[][]::new);

        simpleRegression.addData(dataArray);
        return simpleRegression;
    }
}
