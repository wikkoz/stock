package com.domain.services;

import com.domain.model.Rate;
import com.google.common.collect.Lists;
import com.rest.api.NbpWebclient;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.time.temporal.ChronoUnit.DAYS;

@AllArgsConstructor
public class WebRatesRepository {
    private final NbpWebclient nbpWebclient;

    public Flux<Rate> updateMissingRates(Flux<Rate> ratesFromDatabase, LocalDate inclusiveStartDate, LocalDate inclusiveEndDate) {
        return ratesFromDatabase
                .map(Rate::getEffectiveDate)
                .collectList()
                .flatMapMany(savedDates -> getRatesBetweenDate(inclusiveStartDate, inclusiveEndDate, savedDates));

    }

    private Flux<Rate> getRatesBetweenDate(LocalDate inclusiveStartDate, LocalDate inclusiveEndDate, List<LocalDate> savedDates) {
        List<List<LocalDate>> datesToGetData = splitDatesInFullPeriods(inclusiveStartDate, inclusiveEndDate, savedDates);

        return datesToGetData.stream()
                .filter(period -> !isWeekend(period))
                .map(nbpWebclient::getRates)
                .reduce(Flux.empty(), Flux::concat);

    }

    private List<List<LocalDate>> splitDatesInFullPeriods(LocalDate inclusiveStartDate, LocalDate inclusiveEndDate, List<LocalDate> savedDates) {
        return IntStream.rangeClosed(0, (int) DAYS.between(inclusiveStartDate, inclusiveEndDate)).boxed()
                .map(inclusiveStartDate::plusDays)
                .filter(date -> !savedDates.contains(date))
                .collect(ArrayList::new,
                        (result, date) -> {
                            if (result.isEmpty() || !continuousDates(result.get(result.size()-1), date)) {
                                result.add(Lists.newArrayList(date));
                            } else
                                result.get(result.size()-1).add(date);
                        },
                        ArrayList::addAll);
    }

    private boolean continuousDates(List<LocalDate> dates, LocalDate date) {
        return dates.stream().max(LocalDate::compareTo)
                .map(savedDate -> savedDate.plusDays(1).equals(date))
                .orElse(false);
    }

    private boolean isWeekend(List<LocalDate> dates) {
        return dates.stream().allMatch(date -> date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY);
    }
}
