package com.kairos.dto.activity.pay_out;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CTARuleTemplateCalulatedPayOutDTO {


    private Long id;
    private String name;
    //Base monday
    private List<Integer> days;
    private List<LocalDate> publicHolidays;
    private int granularity;
    private List<BigInteger> activityIds;
    private List<BigInteger> timeTypeIds;
    private List<CTAIntervalDTO> ctaIntervalDTOS;
    private int minutesFromCta;
    private List<BigInteger> timeTypeIdsWithParentTimeType;
    private boolean calculateScheduledHours;

    private List<Long> plannedTimeIds;
    private List<Long> employmentTypes = new ArrayList<>();
    private String payrollSystem;
    private String payrollType;
    private String accountType;


    public CTARuleTemplateCalulatedPayOutDTO(Long id, String name, int granularity) {
        this.id = id;
        this.name = name;
        this.granularity = granularity;

    }

}
