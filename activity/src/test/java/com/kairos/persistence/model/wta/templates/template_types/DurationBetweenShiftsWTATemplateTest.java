package com.kairos.persistence.model.wta.templates.template_types;

import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.activity.activity_tabs.BalanceSettingActivityTabDTO;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.ObjectUtils.newArrayList;
import static org.junit.Assert.*;

public class DurationBetweenShiftsWTATemplateTest {

    private RuleTemplateSpecificInfo ruleTemplateSpecificInfo;
    static {
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
        System.setProperty("user.timezone", "UTC");
        DateTimeZone.setDefault(DateTimeZone.forTimeZone(TimeZone.getDefault()));
    }


    @Before
    public void init(){
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = new ArrayList<>();
        Date startDate = asDate(ZonedDateTime.of(LocalDate.of(2019, Month.NOVEMBER,20), LocalTime.of(12,30),ZoneId.systemDefault()));
        Date endDate = asDate(ZonedDateTime.of(LocalDate.of(2019, Month.NOVEMBER,20), LocalTime.of(18,30),ZoneId.systemDefault()));
        ShiftActivityDTO shiftActivityDTO = ShiftActivityDTO.builder().startDate(startDate).endDate(endDate).activity(ActivityDTO.builder().balanceSettingsActivityTab(BalanceSettingActivityTabDTO.builder().timeType(TimeTypeEnum.PRESENCE).build()).build()).build();
        Date startDate1 = asDate(ZonedDateTime.of(LocalDate.of(2019, Month.NOVEMBER,20), LocalTime.of(18,30),ZoneId.systemDefault()));
        endDate = asDate(ZonedDateTime.of(LocalDate.of(2019, Month.NOVEMBER,20), LocalTime.of(23,30),ZoneId.systemDefault()));
        ShiftActivityDTO shiftActivityDTO2 = ShiftActivityDTO.builder().startDate(startDate).endDate(endDate).activity(ActivityDTO.builder().balanceSettingsActivityTab(BalanceSettingActivityTabDTO.builder().timeType(TimeTypeEnum.TIME_BANK).build()).build()).build();
        ShiftWithActivityDTO shiftWithActivityDTO = new ShiftWithActivityDTO(startDate1,endDate,newArrayList(shiftActivityDTO,shiftActivityDTO2));
        shiftWithActivityDTOS.add(shiftWithActivityDTO);
        startDate = asDate(ZonedDateTime.of(LocalDate.of(2019, Month.NOVEMBER,22), LocalTime.of(10,30),ZoneId.systemDefault()));
        endDate = asDate(ZonedDateTime.of(LocalDate.of(2019, Month.NOVEMBER,22), LocalTime.of(19,30),ZoneId.systemDefault()));
        shiftActivityDTO = ShiftActivityDTO.builder().startDate(startDate).endDate(endDate).activity(ActivityDTO.builder().balanceSettingsActivityTab(BalanceSettingActivityTabDTO.builder().timeType(TimeTypeEnum.ABSENCE).build()).build()).build();
        shiftWithActivityDTO = new ShiftWithActivityDTO(startDate,endDate,newArrayList(shiftActivityDTO));
        shiftWithActivityDTOS.add(shiftWithActivityDTO);
        startDate = asDate(ZonedDateTime.of(LocalDate.of(2019, Month.NOVEMBER,21), LocalTime.of(11,30),ZoneId.systemDefault()));
        endDate = asDate(ZonedDateTime.of(LocalDate.of(2019, Month.NOVEMBER,21), LocalTime.of(16,30),ZoneId.systemDefault()));
        shiftActivityDTO = ShiftActivityDTO.builder().startDate(startDate).endDate(endDate).activity(ActivityDTO.builder().balanceSettingsActivityTab(BalanceSettingActivityTabDTO.builder().timeType(TimeTypeEnum.PRESENCE).build()).build()).build();
        shiftWithActivityDTO = new ShiftWithActivityDTO(startDate,endDate,newArrayList(shiftActivityDTO));
        ruleTemplateSpecificInfo = RuleTemplateSpecificInfo.builder().shifts(shiftWithActivityDTOS).shift(shiftWithActivityDTO).build();
    }

    @Test
    public void getRestingHoursByTimeType() {
        int duration = new DurationBetweenShiftsWTATemplate().getRestingHoursByTimeType(ruleTemplateSpecificInfo,true);
        Assert.assertEquals(duration,1020);
        duration = new DurationBetweenShiftsWTATemplate().getRestingHoursByTimeType(ruleTemplateSpecificInfo,false);
        Assert.assertEquals(duration,1080);
    }
}