package com.kairos.persistence.model.wta.templates.template_types;

import com.kairos.dto.activity.counter.enums.XAxisConfig;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.shift.ViolatedRulesDTO;
import com.kairos.dto.activity.wta.templates.PhaseTemplateValue;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.country.time_slot.TimeSlot;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.DurationType;
import com.kairos.persistence.model.night_worker.ExpertiseNightWorkerSetting;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.ObjectUtils.newArrayList;
import static com.kairos.constants.AppConstants.WEEKS;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DaysOffAfterASeriesWTATemplateTest {

    private DaysOffAfterASeriesWTATemplate daysOffAfterASeriesWTATemplate;
    private RuleTemplateSpecificInfo ruleTemplateSpecificInfo;
    private List<ShiftWithActivityDTO> shiftWithActivityDTOS;
    private ExpertiseNightWorkerSetting expertiseNightWorkerSetting;

    static {
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
        System.setProperty("user.timezone", "UTC");
        DateTimeZone.setDefault(DateTimeZone.forTimeZone(TimeZone.getDefault()));
    }

    //@Before
    public void init(){
        daysOffAfterASeriesWTATemplate = new DaysOffAfterASeriesWTATemplate("Night shifts in sequence", false, "Night shifts in sequence", 1, WEEKS, 3);
        daysOffAfterASeriesWTATemplate.setPhaseTemplateValues(newArrayList(new PhaseTemplateValue(new BigInteger("15"), "DRAFT", (short) 2, (short) 5, true, false, false,4)));
        expertiseNightWorkerSetting = new ExpertiseNightWorkerSetting(new TimeSlot(23,7),120, DurationType.WEEKS,1,2760, XAxisConfig.HOURS,180l,156l);
        shiftWithActivityDTOS = new ArrayList();
        shiftWithActivityDTOS.add(new ShiftWithActivityDTO(asDate(LocalDate.now().minusDays(4), LocalTime.of(15,0)),asDate(LocalDate.now().minusDays(3), LocalTime.of(3,0)),newArrayList(new ShiftActivityDTO(asDate(LocalDate.now().minusDays(4), LocalTime.of(15,0)),asDate(LocalDate.now().minusDays(3), LocalTime.of(3,0))))));
        shiftWithActivityDTOS.add(new ShiftWithActivityDTO(asDate(LocalDate.now().minusDays(3), LocalTime.of(18,0)),asDate(LocalDate.now().minusDays(3), LocalTime.of(23,0)),newArrayList(new ShiftActivityDTO(asDate(LocalDate.now().minusDays(3), LocalTime.of(18,0)),asDate(LocalDate.now().minusDays(3), LocalTime.of(23,0))))));
        shiftWithActivityDTOS.add(new ShiftWithActivityDTO(asDate(LocalDate.now().minusDays(2), LocalTime.of(14,0)),asDate(LocalDate.now().minusDays(1), LocalTime.of(1,0)),newArrayList(new ShiftActivityDTO(asDate(LocalDate.now().minusDays(2), LocalTime.of(14,0)),asDate(LocalDate.now().minusDays(1), LocalTime.of(1,0))))));
        shiftWithActivityDTOS.add(new ShiftWithActivityDTO(asDate(LocalDate.now().minusDays(1), LocalTime.of(12,0)),asDate(LocalDate.now(), LocalTime.of(1,0)),newArrayList(new ShiftActivityDTO(asDate(LocalDate.now().minusDays(1), LocalTime.of(12,0)),asDate(LocalDate.now(), LocalTime.of(1,0))))));
        shiftWithActivityDTOS.add(new ShiftWithActivityDTO(asDate(LocalDate.now().plusDays(1), LocalTime.of(15,0)),asDate(LocalDate.now().plusDays(2), LocalTime.of(3,0)),newArrayList(new ShiftActivityDTO(asDate(LocalDate.now().plusDays(1), LocalTime.of(15,0)),asDate(LocalDate.now().plusDays(2), LocalTime.of(3,0))))));
        shiftWithActivityDTOS.add(new ShiftWithActivityDTO(asDate(LocalDate.now().plusDays(2), LocalTime.of(15,0)),asDate(LocalDate.now().plusDays(2), LocalTime.of(21,0)),newArrayList(new ShiftActivityDTO(asDate(LocalDate.now().plusDays(2), LocalTime.of(15,0)),asDate(LocalDate.now().plusDays(2), LocalTime.of(21,0))))));
        shiftWithActivityDTOS.add(new ShiftWithActivityDTO(asDate(LocalDate.now().plusDays(3), LocalTime.of(15,0)),asDate(LocalDate.now().plusDays(4), LocalTime.of(2,0)),newArrayList(new ShiftActivityDTO(asDate(LocalDate.now().plusDays(3), LocalTime.of(15,0)),asDate(LocalDate.now().plusDays(4), LocalTime.of(2,0))))));
        ruleTemplateSpecificInfo = new RuleTemplateSpecificInfo(shiftWithActivityDTOS,true,expertiseNightWorkerSetting,new BigInteger("15"),new ShiftWithActivityDTO(asDate(LocalDate.now(), LocalTime.of(7,0)),asDate(LocalDate.now().plusDays(1), LocalTime.of(2,0)),newArrayList(new ShiftActivityDTO(asDate(LocalDate.now(), LocalTime.of(7,0)),asDate(LocalDate.now().plusDays(1), LocalTime.of(2,0))))),new UserAccessRoleDTO(true,false),new ViolatedRulesDTO());
    }

    //@Test
    public void validateRules() {
        when(UserContext.getUserDetails().isManagement()).thenReturn(true);
        daysOffAfterASeriesWTATemplate.validateRules(ruleTemplateSpecificInfo);
    }
}