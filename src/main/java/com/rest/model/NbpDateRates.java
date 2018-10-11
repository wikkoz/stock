package com.rest.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class NbpDateRates {
    private String table;
    private String no;
    private LocalDate effectiveDate;
    private List<NbpRate> rates;
}
