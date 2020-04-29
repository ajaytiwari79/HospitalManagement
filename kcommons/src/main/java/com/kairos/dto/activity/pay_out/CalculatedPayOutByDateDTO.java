package com.kairos.dto.activity.pay_out;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.LocalDate;


/*
* Created By Pradeep singh rajawat
*  Date-27/01/2018
*
* */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CalculatedPayOutByDateDTO {

    private LocalDate date;
    private int payOutMin;
}
