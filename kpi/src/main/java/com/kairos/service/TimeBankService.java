package com.kairos.service;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.shift.PlannedTime;
import com.kairos.dto.activity.shift.StaffEmploymentDetails;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.employment.EmploymentLinesDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.Day;
import com.kairos.enums.kpi.CalculationType;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.persistence.model.DailyTimeBankEntry;
import com.kairos.persistence.model.ShiftDataHelper;
import com.kairos.persistence.repository.counter.CounterHelperRepository;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.CommonsExceptionUtil.throwException;
import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.DateUtils.getHourMinutesByMinutes;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.AppConstants.EVERYDAY;
import static com.kairos.constants.KPIMessagesConstants.ERROR_DAYTYPE_NOTFOUND;
import static com.kairos.enums.phase.PhaseDefaultName.*;

@Service
public class TimeBankService implements KPIService{

    @Inject public CounterHelperRepository counterHelperRepository;


    public double getTotalTimeBankOrContractual(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo, boolean calculateContractual) {
        double totalTimeBankOrContractual = 0;
        for (StaffKpiFilterDTO staffKpiFilterDTO : kpiCalculationRelatedInfo.getStaffKPIFilterDTO(staffId)) {
            for (EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO : staffKpiFilterDTO.getEmployment()) {
                Collection<DailyTimeBankEntry> dailyTimeBankEntries = kpiCalculationRelatedInfo.getDailyTimeBankEntrysByEmploymentIdAndInterval(employmentWithCtaDetailsDTO.getId(), dateTimeInterval);
                int timeBankOfInterval = (int) calculateDeltaTimeBankForInterval(kpiCalculationRelatedInfo.getPlanningPeriodInterval(), new Interval(dateTimeInterval.getStartDate().getTime(), dateTimeInterval.getEndDate().getTime()), employmentWithCtaDetailsDTO, new HashSet<>(), (List) dailyTimeBankEntries, calculateContractual)[0];
                totalTimeBankOrContractual += timeBankOfInterval;
            }
        }
        return getHoursByMinutes(totalTimeBankOrContractual);
    }

    @Override
    public <T> double get(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo, T t) {
        return getTotalTimeBankOrContractual(staffId, dateTimeInterval, kpiCalculationRelatedInfo,kpiCalculationRelatedInfo.getCalculationType().equals(CalculationType.STAFFING_LEVEL_CAPACITY));
    }
    public Long calculateActualTimebank(DateTimeInterval dateTimeInterval, Map<LocalDate, DailyTimeBankEntry> dateDailyTimeBankEntryMap, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO, java.time.LocalDate endDate, java.time.LocalDate employmentStartDate, ShiftDataHelper shiftDataHelper) {
        Map<java.time.LocalDate, PhaseDefaultName> datePhaseDefaultNameMap = shiftDataHelper.getDateAndPhaseDefaultName();
        Map<java.time.LocalDate, Boolean> publishPlanningPeriodDateMap = shiftDataHelper.getDateAndPublishPlanningPeriod();
        return getActualTimebank(dateTimeInterval, employmentWithCtaDetailsDTO, endDate, employmentStartDate, publishPlanningPeriodDateMap, dateDailyTimeBankEntryMap, datePhaseDefaultNameMap);
    }

    private long getActualTimebank(DateTimeInterval dateTimeInterval, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO, LocalDate endDate, java.time.LocalDate employmentStartDate, Map<java.time.LocalDate, Boolean> publishPlanningPeriodDateMap, Map<java.time.LocalDate, DailyTimeBankEntry> dateDailyTimeBankEntryMap, Map<java.time.LocalDate, PhaseDefaultName> datePhaseDefaultNameMap) {
        long actualTimebank = employmentWithCtaDetailsDTO.getAccumulatedTimebankMinutes();
        endDate = isNull(employmentWithCtaDetailsDTO.getEndDate()) ? endDate : endDate.isBefore(employmentWithCtaDetailsDTO.getEndDate()) ? endDate : employmentWithCtaDetailsDTO.getEndDate();
        Set<PhaseDefaultName> validPhaseForActualTimeBank = newHashSet(REALTIME, TIME_ATTENDANCE, PAYROLL);
        while (employmentStartDate.isBefore(endDate) || employmentStartDate.equals(endDate)) {
            int deltaTimeBankMinutes = (-getContractualMinutesByDate(dateTimeInterval, employmentStartDate, employmentWithCtaDetailsDTO.getEmploymentLines()));
            if (dateDailyTimeBankEntryMap.containsKey(employmentStartDate) && dateDailyTimeBankEntryMap.get(employmentStartDate).isPublishedSomeActivities()) {
                DailyTimeBankEntry dailyTimeBankEntry = dateDailyTimeBankEntryMap.get(employmentStartDate);
                deltaTimeBankMinutes = dailyTimeBankEntry.getDeltaAccumulatedTimebankMinutes();
                actualTimebank += deltaTimeBankMinutes;
            } else if (validPhaseForActualTimeBank.contains(datePhaseDefaultNameMap.get(employmentStartDate)) || publishPlanningPeriodDateMap.get(employmentStartDate)) {
                actualTimebank += deltaTimeBankMinutes;
            }
            if(dateDailyTimeBankEntryMap.containsKey(employmentStartDate)){
                DailyTimeBankEntry dailyTimeBankEntry = dateDailyTimeBankEntryMap.get(employmentStartDate);
                actualTimebank -= dailyTimeBankEntry.getTimeBankOffMinutes();
                actualTimebank += dailyTimeBankEntry.getProtectedDaysOffMinutes();
            }
            employmentStartDate = employmentStartDate.plusDays(1);
        }
        return actualTimebank;
    }
    public Object[] calculateDeltaTimeBankForInterval(DateTimeInterval planningPeriodInterval, Interval interval, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO,Set<DayOfWeek> dayOfWeeks,List<DailyTimeBankEntry> dailyTimeBankEntries, boolean calculateContractual) {
        Map<String,DailyTimeBankEntry> dailyTimeBanksDatesMap = new HashMap<>();
        if (!calculateContractual) {
            dailyTimeBanksDatesMap = dailyTimeBankEntries.stream().collect(Collectors.toMap(d -> toJodaDateTime(d.getDate()).toLocalDate()+"-"+employmentWithCtaDetailsDTO.getId(),v->v));
        }
        interval = getIntervalValidIntervalForTimebank(employmentWithCtaDetailsDTO, interval, planningPeriodInterval);
        //It can be contractual or Delta Timebank minutes it calculate on the basis of calculateContractual param
        BigDecimal cost = BigDecimal.valueOf(0);
        int contractualOrDeltaMinutes = 0;
        if (interval != null) {
            DateTime startDate = interval.getStart();
            while (!startDate.isAfter(interval.getEnd())) {
                if(isCollectionEmpty(dayOfWeeks) || dayOfWeeks.contains(asLocalDate(startDate.toDate()).getDayOfWeek())){
                    if (calculateContractual || !dailyTimeBanksDatesMap.containsKey(startDate.toLocalDate()+"-"+employmentWithCtaDetailsDTO.getId())) {
                        boolean vaild = (employmentWithCtaDetailsDTO.getWorkingDaysInWeek() == 7) || (startDate.getDayOfWeek() != DateTimeConstants.SATURDAY && startDate.getDayOfWeek() != DateTimeConstants.SUNDAY);
                        if (vaild) {
                            int contractualMin = getContractualMinutesByDate(planningPeriodInterval, DateUtils.asLocalDate(startDate), employmentWithCtaDetailsDTO.getEmploymentLines());
                            if(!calculateContractual) {
                                contractualOrDeltaMinutes -= contractualMin;
                            }else {
                                contractualOrDeltaMinutes += contractualMin;
                            }
                            cost = cost.add(getCostByByMinutes(employmentWithCtaDetailsDTO.getEmploymentLines(), contractualMin, asLocalDate(startDate)));
                        }
                    }else if(!calculateContractual && dailyTimeBanksDatesMap.containsKey(startDate.toLocalDate()+"-"+employmentWithCtaDetailsDTO.getId())){
                        int contractualMin =  dailyTimeBanksDatesMap.get(startDate.toLocalDate()+"-"+employmentWithCtaDetailsDTO.getId()).getDeltaTimeBankMinutes();
                        contractualOrDeltaMinutes += contractualMin;
                        cost = cost.add(getCostByByMinutes(employmentWithCtaDetailsDTO.getEmploymentLines(),contractualMin,asLocalDate(startDate)));
                    }
                }
                startDate = startDate.plusDays(1);
            }
        }
        return new Object[]{contractualOrDeltaMinutes,cost};
    }

    public Interval getIntervalValidIntervalForTimebank(EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO, Interval interval, DateTimeInterval planningPeriodInterval) {
        DateTime employmentStartTime = toJodaDateTime(employmentWithCtaDetailsDTO.getStartDate());
        DateTime employmentEndTime = toJodaDateTime(isNull(employmentWithCtaDetailsDTO.getEndDate()) ? planningPeriodInterval.getEndLocalDate() : employmentWithCtaDetailsDTO.getEndDate().isBefore(planningPeriodInterval.getEndLocalDate()) ? employmentWithCtaDetailsDTO.getEndDate() : planningPeriodInterval.getEndLocalDate());
        Interval employmentInterval = new Interval(employmentStartTime, employmentEndTime);
        return interval.overlap(employmentInterval);
    }

    public int getContractualMinutesByDate(DateTimeInterval planningPeriodInterval, java.time.LocalDate localDate, List<EmploymentLinesDTO> employmentLines) {
        Date date = asDate(localDate);
        int contractualMinutes = 0;
        if (CollectionUtils.isNotEmpty(employmentLines)) {
            if (planningPeriodInterval.contains(date) || planningPeriodInterval.getEndLocalDate().equals(localDate)) {
                for (EmploymentLinesDTO employmentLine : employmentLines) {
                    DateTimeInterval positionInterval = employmentLine.getInterval();
                    if ((positionInterval == null && (employmentLine.getStartDate().equals(localDate) || employmentLine.getStartDate().isBefore(localDate))) || (positionInterval != null && (positionInterval.contains(date) || employmentLine.getEndDate().equals(localDate)))) {
                        contractualMinutes = localDate.getDayOfWeek().getValue() <= employmentLine.getWorkingDaysInWeek() ? employmentLine.getTotalWeeklyMinutes() / employmentLine.getWorkingDaysInWeek() : 0;
                        break;
                    }
                }
            }
        }
        return contractualMinutes;
    }

    public BigDecimal getCostByByMinutes(List<EmploymentLinesDTO> employmentLinesDTOS, int minutes, java.time.LocalDate date){
        BigDecimal hourlyCost = getHourlyCostByDate(employmentLinesDTOS,date);
        BigDecimal oneMinuteCost = hourlyCost.divide(BigDecimal.valueOf(60),BigDecimal.ROUND_CEILING,6);
        return hourlyCost.multiply(BigDecimal.valueOf(getHourByMinutes(minutes))).add(oneMinuteCost.multiply(BigDecimal.valueOf(getHourMinutesByMinutes(minutes))));
    }

    public BigDecimal getHourlyCostByDate(List<EmploymentLinesDTO> employmentLines, java.time.LocalDate localDate) {
        BigDecimal hourlyCost = BigDecimal.valueOf(0);
        for (EmploymentLinesDTO employmentLine : employmentLines) {
            DateTimeInterval positionInterval = employmentLine.getInterval();
            if ((positionInterval == null && (employmentLine.getStartDate().equals(localDate) || employmentLine.getStartDate().isBefore(localDate))) || (positionInterval != null && (positionInterval.contains(asDate(localDate)) || employmentLine.getEndDate().equals(localDate)))) {
                hourlyCost = employmentLine.getHourlyCost();
                break;
            }
        }
        return hourlyCost;
    }

    public boolean isDayTypeValid(Date shiftDate, CTARuleTemplateDTO ruleTemplateDTO, Map<BigInteger, DayTypeDTO> dayTypeDTOMap) {
        List<DayTypeDTO> dayTypeDTOS = ruleTemplateDTO.getDayTypeIds().stream().map(dayTypeDTOMap::get).collect(Collectors.toList());
        boolean valid = false;
        for (DayTypeDTO dayTypeDTO : dayTypeDTOS) {
            if (dayTypeDTO.isHolidayType()) {
                valid = isPublicHolidayValid(shiftDate, valid, dayTypeDTO);
            } else {
                valid = ruleTemplateDTO.getDays() != null && ruleTemplateDTO.getDays().contains(asLocalDate(shiftDate).getDayOfWeek());
            }
            if (valid) {
                break;
            }
        }
        return valid;
    }

    public static boolean isPublicHolidayValid(Date shiftDate, boolean valid, DayTypeDTO dayTypeDTO) {
        for (CountryHolidayCalenderDTO countryHolidayCalenderDTO : dayTypeDTO.getCountryHolidayCalenderData()) {
            DateTimeInterval dateTimeInterval;
            if (dayTypeDTO.isAllowTimeSettings()) {
                LocalTime holidayEndTime = countryHolidayCalenderDTO.getEndTime().get(ChronoField.MINUTE_OF_DAY) == 0 ? LocalTime.MAX : countryHolidayCalenderDTO.getEndTime();
                dateTimeInterval = new DateTimeInterval(asDate(countryHolidayCalenderDTO.getHolidayDate(), countryHolidayCalenderDTO.getStartTime()), asDate(countryHolidayCalenderDTO.getHolidayDate(), holidayEndTime));
            } else {
                dateTimeInterval = new DateTimeInterval(asDate(countryHolidayCalenderDTO.getHolidayDate()), asDate(countryHolidayCalenderDTO.getHolidayDate().plusDays(1)));
            }
            valid = dateTimeInterval.contains(shiftDate);
            if (valid) {
                break;
            }
        }
        return valid;
    }

    public static void setDayTypeToCTARuleTemplate(StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        Map<BigInteger, List<Day>> daytypesMap = staffAdditionalInfoDTO.getDayTypes().stream().collect(Collectors.toMap(k -> k.getId(), v -> v.getValidDays()));
        staffAdditionalInfoDTO.getEmployment().getCtaRuleTemplates().forEach(ctaRuleTemplateDTO -> updateDayTypeDetailInCTARuletemplate(daytypesMap, ctaRuleTemplateDTO));
    }
    public static void updateDayTypeDetailInCTARuletemplate(Map<BigInteger, List<Day>> daytypesMap, CTARuleTemplateDTO ctaRuleTemplateDTO) {
        Set<DayOfWeek> dayOfWeeks = new HashSet<>();
        for (BigInteger dayTypeId : ctaRuleTemplateDTO.getDayTypeIds()) {
            List<Day> applicableDaysOfWeek = daytypesMap.get(dayTypeId);
            if (applicableDaysOfWeek == null) {
                throwException(ERROR_DAYTYPE_NOTFOUND, dayTypeId);
            }
            applicableDaysOfWeek.forEach(day -> {
                if (!day.name().equals(EVERYDAY)) {
                    dayOfWeeks.add(DayOfWeek.valueOf(day.name()));
                }
            });
        }
        ctaRuleTemplateDTO.setPublicHolidays(new ArrayList<>());
        ctaRuleTemplateDTO.setDays(new ArrayList<>(dayOfWeeks));
    }
    public boolean validateCTARuleTemplate(CTARuleTemplateDTO ctaRuleTemplateDTO, StaffEmploymentDetails staffEmploymentDetails, BigInteger shiftPhaseId, Set<BigInteger> activityIds, Set<BigInteger> timeTypeIds, List<PlannedTime> plannedTimes) {
        return ctaRuleTemplateDTO.isRuleTemplateValid(staffEmploymentDetails.getEmploymentType().getId(), shiftPhaseId, activityIds, timeTypeIds, plannedTimes);
    }
}
