package com.kairos.dto.activity.shift;


import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.activity.activity_tabs.BalanceSettingActivityTabDTO;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigInteger;
import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.ObjectUtils.newArrayList;
@RunWith(MockitoJUnitRunner.class)
public class ShiftDTOTest {

    private ShiftWithActivityDTO shiftWithActivityDTO;

    static {
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
        System.setProperty("user.timezone", "UTC");
        DateTimeZone.setDefault(DateTimeZone.forTimeZone(TimeZone.getDefault()));
    }


    @Before
    public void init(){
        List<ShiftActivityDTO> shiftActivityDTOS = new ArrayList();
        Date startDate = asDate(ZonedDateTime.of(LocalDate.of(2019, Month.NOVEMBER,20), LocalTime.of(5,30), ZoneId.systemDefault()));
        Date endDate = asDate(ZonedDateTime.of(LocalDate.of(2019, Month.NOVEMBER,20), LocalTime.of(10,30),ZoneId.systemDefault()));
        shiftActivityDTOS.add(ShiftActivityDTO.builder().startDate(startDate).endDate(endDate).activityId(BigInteger.valueOf(15)).build());
        startDate = asDate(ZonedDateTime.of(LocalDate.of(2019, Month.NOVEMBER,20), LocalTime.of(10,30),ZoneId.systemDefault()));
        endDate = asDate(ZonedDateTime.of(LocalDate.of(2019, Month.NOVEMBER,20), LocalTime.of(12,30),ZoneId.systemDefault()));
        shiftActivityDTOS.add(ShiftActivityDTO.builder().startDate(startDate).endDate(endDate).activityId(BigInteger.valueOf(15)).build());
        startDate = asDate(ZonedDateTime.of(LocalDate.of(2019, Month.NOVEMBER,20), LocalTime.of(12,30),ZoneId.systemDefault()));
        endDate = asDate(ZonedDateTime.of(LocalDate.of(2019, Month.NOVEMBER,20), LocalTime.of(15,30),ZoneId.systemDefault()));
        shiftActivityDTOS.add(ShiftActivityDTO.builder().startDate(startDate).endDate(endDate).activityId(BigInteger.valueOf(116)).build());
        startDate = asDate(ZonedDateTime.of(LocalDate.of(2019, Month.NOVEMBER,20), LocalTime.of(15,30),ZoneId.systemDefault()));
        endDate = asDate(ZonedDateTime.of(LocalDate.of(2019, Month.NOVEMBER,20), LocalTime.of(18,30),ZoneId.systemDefault()));
        shiftActivityDTOS.add(ShiftActivityDTO.builder().startDate(startDate).endDate(endDate).activityId(BigInteger.valueOf(12)).build());
        startDate = asDate(ZonedDateTime.of(LocalDate.of(2019, Month.NOVEMBER,20), LocalTime.of(18,30),ZoneId.systemDefault()));
        endDate = asDate(ZonedDateTime.of(LocalDate.of(2019, Month.NOVEMBER,20), LocalTime.of(23,30),ZoneId.systemDefault()));
        shiftActivityDTOS.add(ShiftActivityDTO.builder().startDate(startDate).endDate(endDate).activityId(BigInteger.valueOf(12)).build());
        shiftWithActivityDTO = new ShiftWithActivityDTO(startDate,endDate,shiftActivityDTOS);
    }

    @Test
    public void mergeShiftActivity() {
        shiftWithActivityDTO.mergeShiftActivity();

    }
}