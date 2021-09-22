package com.kairos.persistence.model;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.dto.activity.cta.CompensationTableInterval;
import com.kairos.dto.activity.shift.PlannedTime;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.StaffEmploymentDetails;
import com.kairos.dto.user.country.agreement.cta.CompensationMeasurementType;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.employment.EmploymentLinesDTO;
import com.kairos.service.TimeBankService;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.temporal.ChronoField;
import java.util.*;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.*;

public class CalculatePlannedHoursAndScheduledHours {

    private TimeBankService timeBankService;

    public CalculatePlannedHoursAndScheduledHours(TimeBankService timeBankService) {
        this.timeBankService = timeBankService;
    }

    public List<ShiftActivityDTO> getShiftActivityByBreak(List<ShiftActivityDTO> shiftActivities, List<ShiftActivityDTO> breakActivities) {
        List<ShiftActivityDTO> updatedShiftActivities = new ArrayList<>();
        if(isCollectionNotEmpty(breakActivities)){
            for (ShiftActivityDTO shiftActivity : shiftActivities) {
                boolean scheduledHourAdded = false;
                boolean anybreakFallOnShiftActivity = breakActivities.stream().anyMatch(breakActivity -> shiftActivity.getInterval().overlaps(breakActivity.getInterval()) && shiftActivity.getInterval().overlap(breakActivity.getInterval()).getMinutes() == breakActivity.getInterval().getMinutes() && !breakActivity.isBreakNotHeld());
                if(anybreakFallOnShiftActivity){
                    for (ShiftActivityDTO breakActivity : breakActivities) {
                        scheduledHourAdded = getShiftActivityByBreakInterval(updatedShiftActivities, shiftActivity, scheduledHourAdded, breakActivity);
                    }
                }else {
                    updatedShiftActivities.add(shiftActivity);
                }
            }

        } else {
            updatedShiftActivities = shiftActivities;
        }
        Collections.sort(updatedShiftActivities);
        return updatedShiftActivities;
    }

    private boolean getShiftActivityByBreakInterval(List<ShiftActivityDTO> updatedShiftActivities, ShiftActivityDTO shiftActivity, boolean scheduledHourAdded, ShiftActivityDTO breakActivity) {
        List<ActivityDTO> activityDTOS = timeBankService.counterHelperRepository.findAllActivitiesByIds(newHashSet(breakActivity.getActivityId()));
        List<DateTimeInterval> dateTimeIntervals = shiftActivity.getInterval().minusInterval(breakActivity.getInterval());
        scheduledHourAdded = updateShiftActivityByBreakInterval(updatedShiftActivities, shiftActivity, dateTimeIntervals, scheduledHourAdded);
        List<PlannedTime> plannedTimes = new ArrayList<>();
        for (PlannedTime plannedTime : shiftActivity.getPlannedTimes()) {
            if (breakActivity.getInterval().overlaps(plannedTime.getInterval())) {
                DateTimeInterval breakDateTimeInterval = breakActivity.getInterval().overlap(plannedTime.getInterval());
                PlannedTime breakPlannedTime = ObjectMapperUtils.copyPropertiesByMapper(plannedTime, PlannedTime.class);
                breakPlannedTime.setStartDate(breakDateTimeInterval.getStartDate());
                breakPlannedTime.setEndDate(breakDateTimeInterval.getEndDate());
                plannedTimes.add(breakPlannedTime);
            }
        }
        breakActivity.setActivity(activityDTOS.get(0));
        breakActivity.setPlannedTimes(plannedTimes);
        breakActivity.setStatus(shiftActivity.getStatus());
        updatedShiftActivities.add(breakActivity);
        return scheduledHourAdded;
    }

    private boolean updateShiftActivityByBreakInterval(List<ShiftActivityDTO> updatedShiftActivities, ShiftActivityDTO shiftActivity, List<DateTimeInterval> dateTimeIntervals,boolean scheduledHourAdded) {
        for (DateTimeInterval timeInterval : dateTimeIntervals) {
            ShiftActivityDTO updatedShiftActivity = ObjectMapperUtils.copyPropertiesByMapper(shiftActivity, ShiftActivityDTO.class);
            updatedShiftActivity.setStartDate(timeInterval.getStartDate());
            updatedShiftActivity.setEndDate(timeInterval.getEndDate());
            List<PlannedTime> plannedTimes = new ArrayList<>();
            for (PlannedTime plannedTime : updatedShiftActivity.getPlannedTimes()) {
                if (plannedTime.getInterval().overlaps(timeInterval)) {
                    DateTimeInterval plannedTimeInterval = plannedTime.getInterval().overlap(timeInterval);
                    plannedTime.setStartDate(plannedTimeInterval.getStartDate());
                    plannedTime.setEndDate(plannedTimeInterval.getEndDate());
                    plannedTimes.add(plannedTime);
                }
            }
            updatedShiftActivity.setActivity(shiftActivity.getActivity());
            updatedShiftActivity.setPlannedTimes(plannedTimes);
            updatedShiftActivity.setStatus(shiftActivity.getStatus());
            if(scheduledHourAdded){
                updatedShiftActivity.setScheduledMinutes(0);
                updatedShiftActivity.setDurationMinutes(0);
            }else{
                scheduledHourAdded = true;
            }
            updatedShiftActivities.add(updatedShiftActivity);
        }
        return scheduledHourAdded;
    }


    public Number getAndUpdateCtaBonusMinutes(DateTimeInterval dateTimeInterval, CTARuleTemplateDTO ruleTemplate, ShiftActivityDTO shiftActivityDTO, StaffEmploymentDetails staffEmploymentDetails, Map<BigInteger, DayTypeDTO> dayTypeDTOMap) {
        Double ctaBonusAndScheduledMinutes = 0.0;
        for (PlannedTime plannedTime : shiftActivityDTO.getPlannedTimes()) {
            if (ruleTemplate.getPlannedTimeIds().contains(plannedTime.getPlannedTimeId()) && timeBankService.isDayTypeValid(plannedTime.getStartDate(),ruleTemplate,dayTypeDTOMap)) {
                DateTimeInterval shiftInterval = dateTimeInterval.overlap(new DateTimeInterval(plannedTime.getStartDate(), plannedTime.getEndDate()));
                ctaBonusAndScheduledMinutes += calculateBonusAndUpdateShiftActivity(dateTimeInterval, ruleTemplate, shiftInterval, staffEmploymentDetails);
            }
            if (ruleTemplate.getPlannedTimeIds().contains(plannedTime.getPlannedTimeId()) && dateTimeInterval.getStartLocalDate().isBefore(asLocalDate(plannedTime.getEndDate())) && timeBankService.isDayTypeValid(plannedTime.getEndDate(),ruleTemplate,dayTypeDTOMap)) {
                DateTimeInterval nextDayInterval = new DateTimeInterval(getStartOfDay(plannedTime.getEndDate()), getEndOfDay(plannedTime.getEndDate()));
                DateTimeInterval shiftInterval = nextDayInterval.overlap(new DateTimeInterval(plannedTime.getStartDate(), plannedTime.getEndDate()));
                ctaBonusAndScheduledMinutes += calculateBonusAndUpdateShiftActivity(nextDayInterval, ruleTemplate, shiftInterval, staffEmploymentDetails);
            }
        }
        return ctaBonusAndScheduledMinutes;
    }

    public Double calculateBonusAndUpdateShiftActivity(DateTimeInterval dateTimeInterval,  CTARuleTemplateDTO ruleTemplate, DateTimeInterval shiftInterval, StaffEmploymentDetails staffEmploymentDetails) {
        Double ctaBonusMinutes = 0.0;
        if (isNotNull(shiftInterval)) {
            ctaBonusMinutes = calculateCTARuleTemplateBonus(ruleTemplate, dateTimeInterval, shiftInterval, staffEmploymentDetails);
        }
        return ctaBonusMinutes;
    }

    public Double calculateCTARuleTemplateBonus(CTARuleTemplateDTO ctaRuleTemplateDTO, DateTimeInterval dateTimeInterval, DateTimeInterval shiftDateTimeInterval, StaffEmploymentDetails staffEmploymentDetails) {
        Double ctaTimeBankMin = 0.0;
        if (isNotNull(shiftDateTimeInterval)) {
            Interval shiftInterval = new Interval(shiftDateTimeInterval.getStartDate().getTime(), shiftDateTimeInterval.getEndDate().getTime());
            LOGGER.debug("rule template : {} shiftInterval {}", ctaRuleTemplateDTO.getId(), shiftInterval);
            for (CompensationTableInterval ctaInterval : ctaRuleTemplateDTO.getCompensationTable().getCompensationTableInterval()) {
                List<Interval> intervalOfCTAs = getCTAInterval(ctaInterval, new DateTime(dateTimeInterval.getStartDate()));
                LOGGER.debug("rule template : {} interval size {}", ctaRuleTemplateDTO.getId(), intervalOfCTAs);
                for (Interval intervalOfCTA : intervalOfCTAs) {
                    if (intervalOfCTA.overlaps(shiftInterval)) {
                        int overlapTimeInMin = (int) intervalOfCTA.overlap(shiftInterval).toDuration().getStandardMinutes();
                        if (ctaInterval.getCompensationMeasurementType().equals(CompensationMeasurementType.MINUTES)) {
                            ctaTimeBankMin += ((double) overlapTimeInMin / ctaRuleTemplateDTO.getCompensationTable().getGranularityLevel()) * ctaInterval.getValue();
                            break;
                        } else if (ctaInterval.getCompensationMeasurementType().equals(CompensationMeasurementType.PERCENT)) {
                            ctaTimeBankMin += ((double) Math.round((double) overlapTimeInMin / ctaRuleTemplateDTO.getCompensationTable().getGranularityLevel()) / 100) * ctaInterval.getValue();
                            break;
                        } else if (CompensationMeasurementType.FIXED_VALUE.equals(ctaInterval.getCompensationMeasurementType())) {
                            double value = ((double) overlapTimeInMin / ctaRuleTemplateDTO.getCompensationTable().getGranularityLevel()) * ctaInterval.getValue();
                            ctaTimeBankMin += (double) (!getHourlyCostByDate(staffEmploymentDetails.getEmploymentLines(), dateTimeInterval.getStartLocalDate()).equals(BigDecimal.valueOf(0)) && staffEmploymentDetails.getHourlyCost().equals(0)? BigDecimal.valueOf(value).divide(staffEmploymentDetails.getHourlyCost(), 6, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(60)).intValue() : 0);
                        }

                    }
                }
            }
        }
        return ctaTimeBankMin;
    }

    private List<Interval> getCTAInterval(CompensationTableInterval interval, DateTime startDate) {
        List<Interval> ctaIntervals = new ArrayList<>(2);
        if (interval.getFrom().isAfter(interval.getTo())) {
            ctaIntervals.add(new Interval(startDate.withTimeAtStartOfDay(), startDate.withTimeAtStartOfDay().plusMinutes(interval.getTo().get(ChronoField.MINUTE_OF_DAY))));
            ctaIntervals.add(new Interval(startDate.withTimeAtStartOfDay().plusMinutes(interval.getFrom().get(ChronoField.MINUTE_OF_DAY)), startDate.withTimeAtStartOfDay().plusDays(1)));
        } else if (interval.getFrom().equals(interval.getTo())) {
            ctaIntervals.add(new Interval(startDate.withTimeAtStartOfDay(), startDate.withTimeAtStartOfDay().plusDays(1)));
        } else {
            ctaIntervals.add(new Interval(startDate.withTimeAtStartOfDay().plusMinutes(interval.getFrom().get(ChronoField.MINUTE_OF_DAY)), startDate.withTimeAtStartOfDay().plusMinutes(interval.getTo().get(ChronoField.MINUTE_OF_DAY))));
        }
        return ctaIntervals;
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
}
