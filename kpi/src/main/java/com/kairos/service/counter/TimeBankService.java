package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.dto.activity.shift.PlannedTime;
import com.kairos.dto.activity.shift.StaffEmploymentDetails;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.employment.EmploymentLinesDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.Day;
import com.kairos.persistence.model.DailyTimeBankEntry;
import org.joda.time.Interval;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.CommonsExceptionUtil.throwException;
import static com.kairos.constants.AppConstants.EVERYDAY;
import static com.kairos.constants.KPIMessagesConstants.ERROR_DAYTYPE_NOTFOUND;

@Service
public class TimeBankService implements KPIService{
    @Override
    public <T> double get(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo, T t) {
        return 0;
    }
    public Long calculateActualTimebank(DateTimeInterval planningPeriodInterval, Map<LocalDate, DailyTimeBankEntry> dateDailyTimeBankEntryMap, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO, LocalDate endLocalDate, LocalDate startDate, Object o) {
        return null;
    }
    public int[] calculateDeltaTimeBankForInterval(DateTimeInterval planningPeriodInterval, Interval interval, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO, HashSet<Object> objects, List<DailyTimeBankEntry> dailyTimeBankEntries, boolean b) {
        return new int[0];
    }
    public Long getContractualMinutesByDate(DateTimeInterval planningPeriodInterval, LocalDate asLocalDate, List<EmploymentLinesDTO> employmentLines) {
        return null;
    }
    public BigDecimal getCostByByMinutes(List<EmploymentLinesDTO> employmentLines, int totalCtaBonus, LocalDate startLocalDate) {
        return null;
    }
    public boolean isDayTypeValid(Date startDate, CTARuleTemplateDTO ruleTemplate, Map<BigInteger, DayTypeDTO> dayTypeDTOMap) {
        return false;
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
    public boolean validateCTARuleTemplate(CTARuleTemplateDTO ruleTemplate, StaffEmploymentDetails staffEmploymentDetails, BigInteger phaseId, HashSet<BigInteger> newHashSet, HashSet<BigInteger> newHashSet1, List<PlannedTime> plannedTimes) {
        return false;
    }
}
