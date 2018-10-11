package com.rest.mapper;

import com.domain.model.Rate;
import com.rest.model.NbpDateRates;

import java.util.List;
import java.util.stream.Collectors;

public class NbpRatesMapper {
    public static List<Rate> map(NbpDateRates nbpRates) {
        return nbpRates.getRates()
                .stream()
                .map(rate -> new Rate(rate.getCode(), rate.getMid(), nbpRates.getEffectiveDate()))
                .collect(Collectors.toList());
    }
}
