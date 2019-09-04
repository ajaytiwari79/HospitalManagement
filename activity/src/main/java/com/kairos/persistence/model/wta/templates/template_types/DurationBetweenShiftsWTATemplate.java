package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.enums.*;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.service.shift.ShiftValidatorService.filterShiftsByPlannedTypeAndTimeTypeIds;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.*;
import static org.apache.commons.collections.CollectionUtils.containsAny;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE16
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class DurationBetweenShiftsWTATemplate extends WTABaseRuleTemplate {


    private Set<BigInteger> plannedTimeIds = new HashSet<>();
    private Set<BigInteger> timeTypeIds = new HashSet<>();
    private float recommendedValue;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MINIMUM;

    public DurationBetweenShiftsWTATemplate(String name, boolean disabled, String description) {
        this.name = name;
        this.disabled = disabled;
        this.description = description;

    }

    public DurationBetweenShiftsWTATemplate() {
        this.wtaTemplateType = WTATemplateType.DURATION_BETWEEN_SHIFTS;
    }

    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        if (!isDisabled() && isValidForPhase(infoWrapper.getPhaseId(), this.phaseTemplateValues) && isCollectionNotEmpty(plannedTimeIds) && containsAny(plannedTimeIds, infoWrapper.getShift().getActivities().stream().flatMap(k->k.getPlannedTimes().stream().map(v->v.getPlannedTimeId())).collect(Collectors.toList())) && isCollectionNotEmpty(timeTypeIds) && containsAny(timeTypeIds, infoWrapper.getShift().getActivitiesTimeTypeIds())) {
            int timefromPrevShift = 0;
            List<ShiftWithActivityDTO> shifts = filterShiftsByPlannedTypeAndTimeTypeIds(infoWrapper.getShifts(), timeTypeIds, plannedTimeIds);
            shifts = (List<ShiftWithActivityDTO>)shifts.stream().filter(shiftWithActivityDTO -> DateUtils.asZoneDateTime(shiftWithActivityDTO.getEndDate()).isBefore(DateUtils.asZoneDateTime(infoWrapper.getShift().getStartDate())) || shiftWithActivityDTO.getEndDate().equals(infoWrapper.getShift().getStartDate())).sorted(getShiftStartTimeComparator()).collect(Collectors.toList());
            if(!shifts.isEmpty()){
                if ((String.valueOf(TimeTypes.WORKING_TYPE)).equals(infoWrapper.getShift().getTimeType()) && !isAbsenceTypeShift(shifts, infoWrapper)) {
                    ZonedDateTime prevShiftEnd = DateUtils.asZoneDateTime(shifts.get(shifts.size() - 1).getEndDate());
                    timefromPrevShift = (int) new DateTimeInterval(prevShiftEnd, DateUtils.asZoneDateTime(infoWrapper.getShift().getStartDate())).getMinutes();
                    Integer[] limitAndCounter = getValueByPhaseAndCounter(infoWrapper, getPhaseTemplateValues(), this);
                    boolean isValid = isValid(minMaxSetting, limitAndCounter[0], timefromPrevShift);
                    if (isValid) {
                        shifts = (List<ShiftWithActivityDTO>) infoWrapper.getShifts().stream().filter(shift1 -> infoWrapper.getShift().getEndDate().before(shift1.getStartDate()) || shift1.getStartDate().equals(infoWrapper.getShift().getEndDate())).sorted(getShiftStartTimeComparator()).collect(Collectors.toList());
                        if (!shifts.isEmpty()) {
                            if (!isAbsenceTypeShift(shifts, infoWrapper)) {
                                ZonedDateTime prevShiftstart = DateUtils.asZoneDateTime(shifts.get(0).getStartDate());
                                timefromPrevShift = (int) new DateTimeInterval(DateUtils.asZoneDateTime(infoWrapper.getShift().getEndDate()), prevShiftstart).getMinutes();
                                limitAndCounter = getValueByPhaseAndCounter(infoWrapper, getPhaseTemplateValues(), this);
                                isValid = isValid(minMaxSetting, limitAndCounter[0], timefromPrevShift);
                            }
                        }
                    }
                    brakeRuleTemplateAndUpdateViolationDetails(infoWrapper, limitAndCounter[1], isValid, this, limitAndCounter[2], DurationType.HOURS, getHoursByMinutes(limitAndCounter[0],this.name));
                }
            }
        }
    }

    @Override
    public boolean isCalculatedValueChanged(WTABaseRuleTemplate wtaBaseRuleTemplate) {
        DurationBetweenShiftsWTATemplate durationBetweenShiftsWTATemplate = (DurationBetweenShiftsWTATemplate) wtaBaseRuleTemplate;
        return (this != durationBetweenShiftsWTATemplate) && !(Float.compare(durationBetweenShiftsWTATemplate.recommendedValue, recommendedValue) == 0 && Objects.equals(plannedTimeIds, durationBetweenShiftsWTATemplate.plannedTimeIds) && Objects.equals(timeTypeIds, durationBetweenShiftsWTATemplate.timeTypeIds) && minMaxSetting == durationBetweenShiftsWTATemplate.minMaxSetting && Objects.equals(this.phaseTemplateValues, durationBetweenShiftsWTATemplate.phaseTemplateValues));
    }


    public boolean isAbsenceTypeShift(List<ShiftWithActivityDTO> shiftWithActivityDTOS, RuleTemplateSpecificInfo infoWrapper) {
        if ((String.valueOf(TimeTypes.NON_WORKING_TYPE)).equals(shiftWithActivityDTOS.get(shiftWithActivityDTOS.size() - 1).getActivities().get(0).getTimeType())) {
            if (shiftWithActivityDTOS.size() > 2 && (TimeTypeEnum.ABSENCE).equals(shiftWithActivityDTOS.get(shiftWithActivityDTOS.size() - 2).getActivities().get(0).getActivity().getBalanceSettingsActivityTab().getTimeType()) && (TimeTypeEnum.ABSENCE.equals(infoWrapper.getShift().getActivities().get(0).getActivity().getBalanceSettingsActivityTab().getTimeType()))) {
                return true;
            }
            return true;
        }
        if ((String.valueOf(TimeTypes.WORKING_TYPE)).equals(shiftWithActivityDTOS.get(shiftWithActivityDTOS.size() - 1).getActivities().get(0).getTimeType()) &&  (TimeTypeEnum.ABSENCE).equals(shiftWithActivityDTOS.get(shiftWithActivityDTOS.size() - 1).getActivities().get(0).getActivity().getBalanceSettingsActivityTab().getTimeType()) && (TimeTypeEnum.ABSENCE.equals(infoWrapper.getShift().getActivities().get(0).getActivity().getBalanceSettingsActivityTab().getTimeType()))) {
            return true;
        }

        return false;
    }
}