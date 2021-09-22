package com.kairos.dto.activity.period;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlippingDateDTO implements Serializable {
    private static final long serialVersionUID = -4761179600765911712L;
    private LocalDate date;
    private int hours;
    private int minutes;
    private int seconds;
}
