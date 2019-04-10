package com.kairos.service.time_bank;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.cta.*;
import com.kairos.dto.activity.shift.EmploymentType;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.StaffUnitPositionDetails;
import com.kairos.dto.user.country.agreement.cta.CalculationFor;
import com.kairos.dto.user.country.agreement.cta.CompensationMeasurementType;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.employment.EmploymentLinesDTO;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.ObjectUtils.newArrayList;
import static com.kairos.commons.utils.ObjectUtils.newHashSet;
import static com.kairos.dto.user.country.agreement.cta.CalculationFor.FUNCTIONS;

@RunWith(MockitoJUnitRunner.class)
public class TimeBankCalculationServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeBankCalculationServiceTest.class);

    @InjectMocks
    private TimeBankCalculationService timeBankCalculationService;


    private StaffUnitPositionDetails unitPosition;
    private Interval interval;
    private List<ShiftWithActivityDTO> shifts;
    private Map<String, DailyTimeBankEntry> dailyTimeBankEntryMap;
    private Set<DateTimeInterval> planningPeriodIntervals;
    private List<DayTypeDTO> dayTypeDTOS;

    @Before
    public void init(){
        List<EmploymentLinesDTO> employmentLinesDTOS = new ArrayList<>();
        employmentLinesDTOS.add(new EmploymentLinesDTO(LocalDate.of(2019,4,9),null,5,35,2100,new BigDecimal(0.002)));
        interval = new Interval(asDate(LocalDate.of(2019,4,10)).getTime(),asDate(LocalDate.of(2019,4,11)).getTime());
        CTAResponseDTO ctaResponseDTO = new CTAResponseDTO();
        unitPosition = new StaffUnitPositionDetails(15l,487l, employmentLinesDTOS,ctaResponseDTO);
        unitPosition.setCtaRuleTemplates(getRuleTemplate());
        unitPosition.setEmploymentType(new EmploymentType(54l));
        shifts= getShifts();
        dailyTimeBankEntryMap = new HashMap<>();
        planningPeriodIntervals = newHashSet(new DateTimeInterval(asDate(LocalDate.of(2019,4,8)),asDate(LocalDate.of(2019,4,21))));
        dayTypeDTOS = new ArrayList<>();
    }

    @Test
    public void getTimeBankByInterval() {
        //DailyTimeBankEntry dailyTimeBankEntry = timeBankCalculationService.getTimeBankByInterval(unitPosition,interval,shifts,dailyTimeBankEntryMap,planningPeriodIntervals,dayTypeDTOS);
        //LOGGER.info("daily timebank : {}",dailyTimeBankEntry);
    }

    private List<CTARuleTemplateDTO> getRuleTemplate(){
        List<BigInteger> activityIds = newArrayList(new BigInteger("4"),new BigInteger("5"),new BigInteger("6"));
        Set<BigInteger> timetypeIds = newHashSet(new BigInteger("10"),new BigInteger("11"),new BigInteger("12"));
        Set<BigInteger> plannedTimeTypeIds = newHashSet(new BigInteger("18"),new BigInteger("17"),new BigInteger("13"));
        List<CTARuleTemplatePhaseInfo> phaseInfo = newArrayList(new CTARuleTemplatePhaseInfo(new BigInteger("6")),new CTARuleTemplatePhaseInfo(new BigInteger("7")));
        List<Long> employementType = newArrayList(54l,64l);
        CTARuleTemplateDTO ctaRuleTemplateDTO1 = new CTARuleTemplateDTO("Scheduled hour ruletemplate",new BigInteger("1"),phaseInfo,employementType,activityIds,timetypeIds,plannedTimeTypeIds);
        ctaRuleTemplateDTO1.setCalculationFor(CalculationFor.SCHEDULED_HOURS);
        CTARuleTemplateDTO ctaRuleTemplateDTO2 = new CTARuleTemplateDTO("Evening cta ruletemplate",new BigInteger("2"),phaseInfo,employementType,activityIds,timetypeIds,plannedTimeTypeIds);
        List<CompensationTableInterval> compensationTableIntervals = newArrayList(new CompensationTableInterval(LocalTime.of(19,0),LocalTime.of(23,0),30, CompensationMeasurementType.MINUTES));
        CompensationTable compensationTable = new CompensationTable(5,compensationTableIntervals);
        ctaRuleTemplateDTO2.setCompensationTable(compensationTable);
        ctaRuleTemplateDTO2.setCalculationFor(CalculationFor.BONUS_HOURS);
        CTARuleTemplateDTO ctaRuleTemplateDTO3 = new CTARuleTemplateDTO("Function ruletemplate",new BigInteger("3"),phaseInfo,employementType,activityIds,timetypeIds,plannedTimeTypeIds);
        ctaRuleTemplateDTO3.setStaffFunctions(newArrayList(54l,55l));
        ctaRuleTemplateDTO3.setCalculationFor(FUNCTIONS);
        CTARuleTemplateDTO ctaRuleTemplateDTO4 = new CTARuleTemplateDTO("extra time ruletemplate",new BigInteger("4"),phaseInfo,employementType,activityIds,timetypeIds,plannedTimeTypeIds);
        compensationTableIntervals = newArrayList(new CompensationTableInterval(LocalTime.of(0,0),LocalTime.of(0,0),60, CompensationMeasurementType.FIXED_VALUE));
        compensationTable = new CompensationTable(20,compensationTableIntervals);
        ctaRuleTemplateDTO4.setCompensationTable(compensationTable);
        ctaRuleTemplateDTO4.setCalculationFor(CalculationFor.BONUS_HOURS);
        CTARuleTemplateDTO ctaRuleTemplateDTO5 = new CTARuleTemplateDTO("overtime time ruletemplate",new BigInteger("5"),phaseInfo,employementType,activityIds,timetypeIds,plannedTimeTypeIds);
        compensationTableIntervals = newArrayList(new CompensationTableInterval(LocalTime.of(12,0),LocalTime.of(0,0),50, CompensationMeasurementType.PERCENT));
        compensationTable = new CompensationTable(10,compensationTableIntervals);
        ctaRuleTemplateDTO5.setCompensationTable(compensationTable);
        ctaRuleTemplateDTO5.setCalculationFor(CalculationFor.BONUS_HOURS);
        List<CTARuleTemplateDTO> ctaRuleTemplateDTOS = newArrayList(ctaRuleTemplateDTO1,ctaRuleTemplateDTO2,ctaRuleTemplateDTO3,ctaRuleTemplateDTO4,ctaRuleTemplateDTO5);
        return ctaRuleTemplateDTOS;
    }

    private List<ShiftWithActivityDTO> getShifts(){
        List<ShiftWithActivityDTO> shifts = new ArrayList<>();
        List<ShiftActivityDTO> shiftActivityDTOS = new ArrayList<>();

        shiftActivityDTOS.add(new ShiftActivityDTO("test1",asDate(LocalDateTime.of(2019,4,10,3,00)),asDate(LocalDateTime.of(2019,4,10,9,00)),new BigInteger("4"),360,newHashSet(ShiftStatus.PUBLISH),new ActivityDTO(new BigInteger("10"))));
        shiftActivityDTOS.add(new ShiftActivityDTO("test2",asDate(LocalDateTime.of(2019,4,10,9,00)),asDate(LocalDateTime.of(2019,4,10,15,00)),new BigInteger("5"),360,newHashSet(),new ActivityDTO(new BigInteger("10"))));
        shifts.add(new ShiftWithActivityDTO(new BigInteger("6"),shiftActivityDTOS));
        shiftActivityDTOS = new ArrayList<>();
        shiftActivityDTOS.add(new ShiftActivityDTO("test2",asDate(LocalDateTime.of(2019,4,10,17,00)),asDate(LocalDateTime.of(2019,4,11,3,00)),new BigInteger("5"),360,newHashSet(ShiftStatus.PUBLISH),new ActivityDTO(new BigInteger("10"))));
        shiftActivityDTOS.add(new ShiftActivityDTO("test1",asDate(LocalDateTime.of(2019,4,11,3,00)),asDate(LocalDateTime.of(2019,4,11,9,00)),new BigInteger("4"),360,newHashSet(ShiftStatus.PUBLISH),new ActivityDTO(new BigInteger("10"))));
        shifts.add(new ShiftWithActivityDTO(new BigInteger("7"),shiftActivityDTOS));
        return shifts;
    }

}