package com.kairos.shiftplanningNewVersion.utils;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.cta.CompensationTableInterval;
import com.kairos.dto.user.country.agreement.cta.CalculationFor;
import com.kairos.dto.user.country.agreement.cta.CompensationMeasurementType;
import com.kairos.enums.Day;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.shiftplanning.constraints.activityconstraint.CountryHolidayCalender;
import com.kairos.shiftplanning.constraints.activityconstraint.DayType;
import com.kairos.shiftplanning.domain.activity.ShiftActivity;
import com.kairos.shiftplanning.domain.shift.PlannedTime;
import com.kairos.shiftplanning.domain.staff.BreakSettings;
import com.kairos.shiftplanning.domain.staff.CTARuleTemplate;
import com.kairos.shiftplanning.domain.staff.Employment;
import com.kairos.shiftplanning.domain.staff.EmploymentLine;
import com.kairos.shiftplanningNewVersion.entity.Shift;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.dto.user.country.agreement.cta.CalculationFor.BONUS_HOURS;
import static com.kairos.dto.user.country.agreement.cta.CalculationFor.FUNCTIONS;

public class UpdateTimeAndPayoutDetails {

    public static void updateTimeBankAndPayoutDetails(Shift shift){
        if(isNotNull(shift.getStart())) {
            new CalculatePlannedHoursAndScheduledHours(shift).calculate();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CalculatePlannedHoursAndScheduledHours {
        private DateTimeInterval dateTimeInterval;
        private Shift shift;
        private Map<Long, DayType> dayTypeDTOMap;

        public CalculatePlannedHoursAndScheduledHours(Shift shift) {
            this.dateTimeInterval = new DateTimeInterval(getStartOfDay(shift.getStart()), getEndOfDay(shift.getStart()));
            this.dayTypeDTOMap = shift.getStaff().getUnit().getDayTypeMap();
            this.shift = shift;
        }

        public CalculatePlannedHoursAndScheduledHours calculate() {
            boolean ruleTemplateValid = false;
            for (CTARuleTemplate ruleTemplate : shift.getStaff().getLocalDateCTARuletemplateMap().getOrDefault(shift.getStartDate(),new ArrayList<>())) {
                    ruleTemplateValid = calculateBonusOrScheduledMinutesByShiftActivity(ruleTemplateValid, ruleTemplate, shift);
                if (ruleTemplate.getCalculationFor().equals(FUNCTIONS) && ruleTemplateValid) {
                    BigDecimal value = getFunctionalBonusCompensation(shift.getStaff().getEmployment(), ruleTemplate, dateTimeInterval);
                    shift.getStaff().getFunctionalBonus().put(shift.getStartDate(),value);
                }
            }
            return this;
        }

        private boolean calculateBonusOrScheduledMinutesByShiftActivity(boolean ruleTemplateValid, CTARuleTemplate ruleTemplate, Shift shift) {
            List<ShiftActivity> shiftActivities = getShiftActivityByBreak(shift.getShiftActivities(), shift.getBreakActivities(),shift.getStaff().getBreakSettings());
            for (ShiftActivity shiftActivityForCalculation : shiftActivities) {
                ShiftActivity shiftActivity = getShiftActivityDTO(shift, shiftActivityForCalculation);
                if(isNotNull(shiftActivity)){
                    ruleTemplateValid = validateCTARuleTemplate(ruleTemplate, shift.getStaff().getEmployment(), shift.getStaff().getUnit().getPlanningPeriod().getPhase().getId(), shiftActivity.getActivity().getId(), shiftActivity.getActivity().getTimeType().getId(), shiftActivity.getPlannedTimes());
                    LOGGER.debug("rule template : {} valid {}", ruleTemplate.getId(), ruleTemplateValid);
                    if (ruleTemplateValid) {
                        updateScheduledAndBonusMinutes(ruleTemplate, shiftActivityForCalculation, shiftActivity);
                    }
                }
            }
            return ruleTemplateValid;
        }

        public boolean validateCTARuleTemplate(CTARuleTemplate ctaRuleTemplate, Employment employment, BigInteger shiftPhaseId, BigInteger activityId, BigInteger timeTypeId, List<PlannedTime> plannedTimes) {
            return ctaRuleTemplate.isRuleTemplateValid(employment.getEmploymentType().getId(), shiftPhaseId, activityId, timeTypeId, plannedTimes);
        }

        private void updateScheduledAndBonusMinutes(CTARuleTemplate ruleTemplate, ShiftActivity shiftActivityForCalculation, ShiftActivity shiftActivity) {
            int ctaBonusAndScheduledMinutes = 0;
            if (ruleTemplate.getCalculationFor().equals(CalculationFor.SCHEDULED_HOURS) && dateTimeInterval.contains(shiftActivityForCalculation.getStartDate()) && isDayTypeValid(shiftActivityForCalculation.getStartDate(),ruleTemplate,dayTypeDTOMap)) {
                ctaBonusAndScheduledMinutes = shiftActivityForCalculation.getScheduledMinutes();
                shiftActivity.setScheduledMinutesOfTimebank(shiftActivityForCalculation.getScheduledMinutes() + shiftActivity.getScheduledMinutesOfTimebank());
            } else if (ruleTemplate.getCalculationFor().equals(BONUS_HOURS)) {
                ctaBonusAndScheduledMinutes = (int) Math.round(getAndUpdateCtaBonusMinutes(dateTimeInterval, ruleTemplate, shiftActivityForCalculation,dayTypeDTOMap));
                shiftActivity.updateTimeBankOrPayoutBonus(ruleTemplate,ctaBonusAndScheduledMinutes);
                LOGGER.debug("rule template : {} minutes {}", ruleTemplate.getId(), ctaBonusAndScheduledMinutes);
            }
            shiftActivity.setPlannedMinutesOfTimebank(ctaBonusAndScheduledMinutes + shiftActivity.getPlannedMinutesOfTimebank());
            if ((shiftActivityForCalculation.getStatus().contains(ShiftStatus.PUBLISH))) {
                //Todo if actual timebank need then add the code here
            }
        }


        public ShiftActivity getShiftActivityDTO(Shift shift, ShiftActivity shiftActivity) {
            try {
                return shift.getShiftActivities().stream().filter(shiftActivityDTO1 -> shiftActivityDTO1.getActivity().equals(shiftActivity.getActivity()) || (isCollectionNotEmpty(shift.getBreakActivities()) && shift.getBreakActivities().get(0).getActivity().equals(shiftActivityDTO1.getActivity()))).findAny().get();
            } catch (NullPointerException | NoSuchElementException e) {
            }
            return shiftActivity;
        }

        public List<ShiftActivity> getShiftActivityByBreak(List<ShiftActivity> shiftActivities, List<ShiftActivity> breakActivities, BreakSettings breakSettings) {
            List<ShiftActivity> updatedShiftActivities = new ArrayList<>();
            if(isCollectionNotEmpty(breakActivities)){
                for (ShiftActivity shiftActivity : shiftActivities) {
                    boolean scheduledHourAdded = false;
                    boolean anybreakFallOnShiftActivity = breakActivities.stream().anyMatch(breakActivity -> shiftActivity.getInterval().overlaps(breakActivity.getInterval()) && shiftActivity.getInterval().overlap(breakActivity.getInterval()).getMinutes() == breakActivity.getInterval().getMinutes() && !breakActivity.isBreakNotHeld());
                    if(anybreakFallOnShiftActivity){
                        for (ShiftActivity breakActivity : breakActivities) {
                            scheduledHourAdded = getShiftActivityByBreakInterval(updatedShiftActivities, shiftActivity, scheduledHourAdded, breakActivity,breakSettings);
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

        private boolean getShiftActivityByBreakInterval(List<ShiftActivity> updatedShiftActivities, ShiftActivity shiftActivity, boolean scheduledHourAdded, ShiftActivity breakActivity, BreakSettings breakSettings) {
            List<DateTimeInterval> dateTimeIntervals = shiftActivity.getInterval().minusInterval(breakActivity.getInterval());
            scheduledHourAdded = updateShiftActivityByBreakInterval(updatedShiftActivities, shiftActivity, dateTimeIntervals, scheduledHourAdded);
            List<PlannedTime> plannedTimes = new ArrayList<>();
            for (PlannedTime plannedTime : shiftActivity.getPlannedTimes()) {
                if (breakActivity.getInterval().overlaps(plannedTime.getInterval())) {
                    DateTimeInterval breakDateTimeInterval = breakActivity.getInterval().overlap(plannedTime.getInterval());
                    PlannedTime breakPlannedTime = ObjectMapperUtils.copyPropertiesByMapper(plannedTime, PlannedTime.class);
                    breakPlannedTime.setStartDate(breakDateTimeInterval.getStart());
                    breakPlannedTime.setEndDate(breakDateTimeInterval.getEnd());
                    plannedTimes.add(breakPlannedTime);
                }
            }
            breakActivity.setActivity(breakSettings.getActivity());
            breakActivity.setPlannedTimes(plannedTimes);
            breakActivity.setStatus(shiftActivity.getStatus());
            updatedShiftActivities.add(breakActivity);
            return scheduledHourAdded;
        }

        private boolean updateShiftActivityByBreakInterval(List<ShiftActivity> updatedShiftActivities, ShiftActivity shiftActivity, List<DateTimeInterval> dateTimeIntervals,boolean scheduledHourAdded) {
            for (DateTimeInterval timeInterval : dateTimeIntervals) {
                ShiftActivity updatedShiftActivity = ObjectMapperUtils.copyPropertiesByMapper(shiftActivity, ShiftActivity.class);
                updatedShiftActivity.getActivity().setConstraints(shiftActivity.getActivity().getConstraints());
                updatedShiftActivity.setStartDate(timeInterval.getStart());
                updatedShiftActivity.setEndDate(timeInterval.getEnd());
                List<PlannedTime> plannedTimes = new ArrayList<>();
                for (PlannedTime plannedTime : updatedShiftActivity.getPlannedTimes()) {
                    if (plannedTime.getInterval().overlaps(timeInterval)) {
                        DateTimeInterval plannedTimeInterval = plannedTime.getInterval().overlap(timeInterval);
                        plannedTime.setStartDate(plannedTimeInterval.getStart());
                        plannedTime.setEndDate(plannedTimeInterval.getEnd());
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

        public Double getAndUpdateCtaBonusMinutes(DateTimeInterval dateTimeInterval, CTARuleTemplate ruleTemplate, ShiftActivity shiftActivity,Map<Long,DayType> dayTypeDTOMap) {
            Double ctaBonusAndScheduledMinutes = 0.0;
            for (PlannedTime plannedTime : shiftActivity.getPlannedTimes()) {
                if (ruleTemplate.getPlannedTimeIds().contains(plannedTime.getPlannedTimeId()) && isDayTypeValid(plannedTime.getStartDate(),ruleTemplate,dayTypeDTOMap)) {
                    DateTimeInterval shiftInterval = dateTimeInterval.overlap(new DateTimeInterval(plannedTime.getStartDate(), plannedTime.getEndDate()));
                    ctaBonusAndScheduledMinutes += calculateCTARuleTemplateBonus(ruleTemplate, dateTimeInterval, shiftInterval);
                }
                if (ruleTemplate.getPlannedTimeIds().contains(plannedTime.getPlannedTimeId()) && dateTimeInterval.getStartLocalDate().isBefore(plannedTime.getEndDate().toLocalDate()) && isDayTypeValid(plannedTime.getEndDate(),ruleTemplate,dayTypeDTOMap)) {
                    DateTimeInterval nextDayInterval = new DateTimeInterval(getStartOfDay(plannedTime.getEndDate()), getEndOfDay(plannedTime.getEndDate()));
                    DateTimeInterval shiftInterval = nextDayInterval.overlap(new DateTimeInterval(plannedTime.getStartDate(), plannedTime.getEndDate()));
                    ctaBonusAndScheduledMinutes += calculateCTARuleTemplateBonus(ruleTemplate, dateTimeInterval, shiftInterval);
                }
            }
            return ctaBonusAndScheduledMinutes;
        }


        public BigDecimal getFunctionalBonusCompensation(Employment employment, CTARuleTemplate ctaRuleTemplate, DateTimeInterval dateTimeInterval) {
            BigDecimal value = BigDecimal.valueOf(0);
            if (employment.getDateWiseFunctionMap().containsKey(dateTimeInterval.getStartLocalDate()) && ctaRuleTemplate.getStaffFunctions().contains(employment.getDateWiseFunctionMap().get(dateTimeInterval.getStartLocalDate()).getId())) {
                EmploymentLine employmentLine = employment.getEmploymentLinesByDate(dateTimeInterval.getStartLocalDate());
                BigDecimal hourlyCostByDate = getHourlyCostByDate(employment.getEmploymentLines(), dateTimeInterval.getStartLocalDate());
                value = !hourlyCostByDate.equals(BigDecimal.valueOf(0)) ? BigDecimal.valueOf(ctaRuleTemplate.getCalculateValueAgainst().getFixedValue().getAmount()).divide(employmentLine.getHourlyCost(), 6, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(60)) : BigDecimal.valueOf(0);
            }
            return value;
        }

        public Double calculateCTARuleTemplateBonus(CTARuleTemplate ctaRuleTemplate, DateTimeInterval dateTimeInterval, DateTimeInterval shiftInterval) {
            Double ctaTimeBankMin = 0.0;
            if (isNotNull(shiftInterval)) {
                LOGGER.debug("rule template : {} shiftInterval {}", ctaRuleTemplate.getId(), shiftInterval);
                for (CompensationTableInterval ctaInterval : ctaRuleTemplate.getCompensationTable().getCompensationTableInterval()) {
                    List<DateTimeInterval> intervalOfCTAs = getCTAInterval(ctaInterval, dateTimeInterval.getStart());
                    LOGGER.debug("rule template : {} interval size {}", ctaRuleTemplate.getId(), intervalOfCTAs);
                    for (DateTimeInterval intervalOfCTA : intervalOfCTAs) {
                        if (intervalOfCTA.overlaps(shiftInterval)) {
                            int overlapTimeInMin = (int) intervalOfCTA.overlap(shiftInterval).getMinutes();
                            if (ctaInterval.getCompensationMeasurementType().equals(CompensationMeasurementType.MINUTES)) {
                                ctaTimeBankMin += ((double) overlapTimeInMin / ctaRuleTemplate.getCompensationTable().getGranularityLevel()) * ctaInterval.getValue();
                                break;
                            } else if (ctaInterval.getCompensationMeasurementType().equals(CompensationMeasurementType.PERCENT)) {
                                ctaTimeBankMin += ((double) Math.round((double) overlapTimeInMin / ctaRuleTemplate.getCompensationTable().getGranularityLevel()) / 100) * ctaInterval.getValue();
                                break;
                            } else if (CompensationMeasurementType.FIXED_VALUE.equals(ctaInterval.getCompensationMeasurementType())) {
                                EmploymentLine employmentLine = shift.getStaff().getEmployment().getEmploymentLinesByDate(dateTimeInterval.getStartLocalDate());
                                double value = ((double) overlapTimeInMin / ctaRuleTemplate.getCompensationTable().getGranularityLevel()) * ctaInterval.getValue();
                                ctaTimeBankMin += (double) (!getHourlyCostByDate(this.shift.getStaff().getEmployment().getEmploymentLines(), dateTimeInterval.getStartLocalDate()).equals(BigDecimal.valueOf(0)) && employmentLine.getHourlyCost().equals(0)? BigDecimal.valueOf(value).divide(employmentLine.getHourlyCost(), 6, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(60)).intValue() : 0);
                            }

                        }
                    }
                }
            }
            return ctaTimeBankMin;
        }

        public BigDecimal getHourlyCostByDate(List<EmploymentLine> employmentLines, java.time.LocalDate localDate) {
            BigDecimal hourlyCost = BigDecimal.valueOf(0);
            for (EmploymentLine employmentLine : employmentLines) {
                DateTimeInterval positionInterval = employmentLine.getInterval();
                if ((positionInterval == null && (employmentLine.getStartDate().equals(localDate) || employmentLine.getStartDate().isBefore(localDate))) || (positionInterval != null && (positionInterval.contains(asDate(localDate)) || employmentLine.getEndDate().equals(localDate)))) {
                    hourlyCost = employmentLine.getHourlyCost();
                    break;
                }
            }
            return hourlyCost;
        }

        private List<DateTimeInterval> getCTAInterval(CompensationTableInterval interval, ZonedDateTime startDate) {
            List<DateTimeInterval> ctaIntervals = new ArrayList<>(2);
            ZonedDateTime startOfDay = getStartOfDay(startDate);
            if (interval.getFrom().isAfter(interval.getTo())) {
                ctaIntervals.add(new DateTimeInterval(startOfDay, startOfDay.plusMinutes(interval.getTo().get(ChronoField.MINUTE_OF_DAY))));
                ctaIntervals.add(new DateTimeInterval(startOfDay.plusMinutes(interval.getFrom().get(ChronoField.MINUTE_OF_DAY)), startOfDay.plusDays(1)));
            } else if (interval.getFrom().equals(interval.getTo())) {
                ctaIntervals.add(new DateTimeInterval(startOfDay, startOfDay.plusDays(1)));
            } else {
                ctaIntervals.add(new DateTimeInterval(startOfDay.plusMinutes(interval.getFrom().get(ChronoField.MINUTE_OF_DAY)), startOfDay.plusMinutes(interval.getTo().get(ChronoField.MINUTE_OF_DAY))));
            }
            return ctaIntervals;
        }

        public boolean isDayTypeValid(ZonedDateTime shiftDate, CTARuleTemplate ctaRuleTemplate, Map<Long, DayType> dayTypeDTOMap) {
            List<DayType> dayTypes = ctaRuleTemplate.getDayTypeIds().stream().map(dayTypeDTOMap::get).collect(Collectors.toList());
            boolean valid = false;
            for (DayType dayType : dayTypes) {
                if (dayType.isHolidayType()) {
                    valid = isPublicHolidayValid(shiftDate, valid, dayType);
                } else {
                    valid = dayType.getValidDays() != null && dayType.getValidDays().contains(Day.valueOf(shiftDate.getDayOfWeek().toString()));
                }
                if (valid) {
                    break;
                }
            }
            return valid;
        }

        public boolean isPublicHolidayValid(ZonedDateTime shiftDate, boolean valid, DayType dayType) {
            for (CountryHolidayCalender countryHolidayCalenderDTO : dayType.getCountryHolidayCalenders()) {
                DateTimeInterval dateTimeInterval;
                if (dayType.isAllowTimeSettings()) {
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


    }
}
