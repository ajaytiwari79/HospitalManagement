package com.kairos.dto.activity.time_bank;

import com.kairos.dto.user.country.agreement.cta.CTAIntervalDTO;
import com.kairos.dto.user.country.agreement.cta.CalculationFor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CTARuleTemplateCalulatedTimeBankDTO {


    private Long id;
    private String name;
    //Base monday
    private List<Integer> days;
    private List<LocalDate> publicHolidays;
    private int granularity;
    private List<BigInteger> activityIds;
    private List<BigInteger> timeTypeIds;
    private List<Long> plannedTimeIds;
    private List<CTAIntervalDTO> ctaIntervalDTOS;
    private int minutesFromCta;
    private List<BigInteger> timeTypeIdsWithParentTimeType;
    private boolean calculateScheduledHours;
    private CalculationFor calculationFor;
    private List<Long> employmentTypes = new ArrayList<>();
    private String accountType;
}
