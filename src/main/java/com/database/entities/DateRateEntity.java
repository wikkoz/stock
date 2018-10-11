package com.database.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        indexes = {
                @Index(columnList = "currency, date", name = "curr_idx")
        }
)
public class DateRateEntity extends BaseEntity{
    private String currency;
    private double value;
    private LocalDate date;
}
