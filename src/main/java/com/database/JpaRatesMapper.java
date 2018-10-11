package com.database;

import com.database.entities.DateRateEntity;
import com.domain.model.Rate;

import java.math.BigDecimal;

public class JpaRatesMapper {
    public static Rate map(DateRateEntity dateRateEntity) {
        return new Rate(dateRateEntity.getCurrency(), BigDecimal.valueOf(dateRateEntity.getValue()), dateRateEntity.getDate());
    }

    public static DateRateEntity map(Rate rate) {
        return new DateRateEntity(rate.getCurrency(), rate.getValue().doubleValue(), rate.getEffectiveDate());
    }
}
