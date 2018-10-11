package com.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class Trend {
    private String currency;
    private BigDecimal trend;
}
