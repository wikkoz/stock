package com.domain.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class Rate {
    private final String currency;
    private final BigDecimal value;
    private final LocalDate effectiveDate;
}
