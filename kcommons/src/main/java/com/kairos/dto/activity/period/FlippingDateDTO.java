package com.kairos.dto.activity.period;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlippingDateDTO {
    private LocalDate date;
    private int hours;
    private int minutes;
    private int seconds;
}
