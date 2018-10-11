package com.rest.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@Data
public class NbpRate {
    private String currency;
    private String code;
    private BigDecimal mid;
}
