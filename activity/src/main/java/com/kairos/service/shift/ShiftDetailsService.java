package com.kairos.service.shift;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.shift.PlannedTime;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.shift.WorkTimeAgreementRuleViolation;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.dto.user.reason_code.ReasonCodeWrapper;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.shift.ShiftViolatedRules;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.shift.ShiftViolatedRulesMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.unit_settings.ActivityConfigurationService;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.isEqualOrBefore;
import static com.kairos.commons.utils.ObjectUtils.isNull;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toMap;


/**
 * CreatedBy vipulpandey on 17/12/18
 **/
@Service
@Transactional
public class ShiftDetailsService extends MongoBaseService {

    private final Logger logger = LoggerFactory.getLogger(ShiftDetailsService.class);
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private ShiftViolatedRulesMongoRepository shiftViolatedRulesMongoRepository;
    @Inject
    private PhaseService phaseService;
    @Inject
    private ActivityConfigurationService activityConfigurationService;
    @Inject
    private ShiftService shiftService;

    public List<ShiftWithActivityDTO> shiftDetailsById(Long unitId, List<BigInteger> shiftIds) {
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = shiftMongoRepository.findAllShiftsByIds(shiftIds);
        setReasonCodeAndRuleViolationsInShifts(shiftWithActivityDTOS, unitId, shiftIds);
        return shiftWithActivityDTOS;
    }

    public boolean updateRemarkInShiftActivity(BigInteger shiftActivityId, ShiftActivityDTO shiftActivityDTO) {
        shiftMongoRepository.updateRemarkInShiftActivity(shiftActivityId, shiftActivityDTO.getRemarks());
        return true;
    }

    private void setReasonCodeAndRuleViolationsInShifts(List<ShiftWithActivityDTO> shiftWithActivityDTOS, Long unitId, List<BigInteger> shiftIds) {
        ReasonCodeWrapper reasonCodeWrapper = findReasonCodes(shiftWithActivityDTOS, unitId);
        Map<BigInteger, List<WorkTimeAgreementRuleViolation>> wtaRuleViolationMap = findAllWTAViolatedRules(shiftIds);
        Map<Long, ReasonCodeDTO> reasonCodeDTOMap = reasonCodeWrapper.getReasonCodes().stream().collect(toMap(ReasonCodeDTO::getId, Function.identity()));

        for (ShiftWithActivityDTO shift : shiftWithActivityDTOS) {
            for (ShiftActivityDTO shiftActivityDTO : shift.getActivities()) {
                if (!shiftActivityDTO.isBreakShift()) {
                    shiftActivityDTO.setReasonCode(reasonCodeDTOMap.get(shiftActivityDTO.getAbsenceReasonCodeId()));
                }
                shiftActivityDTO.setLocation(reasonCodeWrapper.getContactAddressData());
            }
            shift.setWtaRuleViolations(wtaRuleViolationMap.get(shift.getId()));
        }
    }

    private Map<BigInteger, List<WorkTimeAgreementRuleViolation>> findAllWTAViolatedRules(List<BigInteger> shiftIds) {
        List<ShiftViolatedRules> shiftViolatedRules = shiftViolatedRulesMongoRepository.findAllViolatedRulesByShiftIds(shiftIds);
        Map<BigInteger, List<WorkTimeAgreementRuleViolation>> wtaRuleViolationMap = shiftViolatedRules.stream().collect(toMap(shiftViolatedRule -> shiftViolatedRule.getShiftId(), v -> v.getWorkTimeAgreements(), (previous, current) -> current));
        return wtaRuleViolationMap;
    }

    private ReasonCodeWrapper findReasonCodes(List<ShiftWithActivityDTO> shiftWithActivityDTOS, Long unitId) {
        Set<Long> absenceReasonCodeIds = shiftWithActivityDTOS.stream().flatMap(shifts -> shifts.getActivities().stream().filter(shiftActivityDTO -> shiftActivityDTO.getAbsenceReasonCodeId() != null).map(shiftActivityDTO -> shiftActivityDTO.getAbsenceReasonCodeId())).collect(Collectors.toSet());
        List<NameValuePair> requestParam = new ArrayList<>();
        requestParam.add(new BasicNameValuePair("absenceReasonCodeIds", absenceReasonCodeIds.toString()));
        return userIntegrationService.getUnitInfoAndReasonCodes(unitId, requestParam);
    }

    public void addPlannedTimeInShift(Shift shiftDTO, ActivityWrapper activityWrapper, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shiftDTO.getUnitId(), shiftDTO.getActivities().get(0).getStartDate(), shiftDTO.getActivities().get(shiftDTO.getActivities().size() - 1).getEndDate());
        BigInteger plannedTimeId = shiftService.addPlannedTimeInShift(shiftDTO.getUnitId(), phase.getId(), activityWrapper.getActivity(), staffAdditionalInfoDTO);
        if (isNull(shiftDTO.getId())) {
            assignedPlannedTimeInActivity(shiftDTO, plannedTimeId);
        } else {
            adjustPlannedTimeInActivity(shiftDTO, plannedTimeId);
        }
    }

    private void assignedPlannedTimeInActivity(Shift shiftDTO, BigInteger plannedTimeId) {
        shiftDTO.getActivities().forEach(shiftActivity -> shiftActivity.setPlannedTimes(Arrays.asList(new PlannedTime(plannedTimeId, shiftActivity.getStartDate(), shiftActivity.getEndDate()))));
    }

    private void adjustPlannedTimeInActivity(Shift shiftDTO, BigInteger plannedTimeId) {
        Shift shift = shiftMongoRepository.findOne(shiftDTO.getId());
        List<PlannedTime> plannedTimeList = shift.getActivities().stream().flatMap(k -> k.getPlannedTimes().stream()).collect(Collectors.toList());
        Map<DateTimeInterval, PlannedTime> plannedTimeMap = plannedTimeList.stream().collect(toMap(k -> new DateTimeInterval(k.getStartDate(), k.getEndDate()), Function.identity()));
        for (ShiftActivity shiftActivityDTO : shiftDTO.getActivities()) {
            shiftActivityDTO.setPlannedTimes(filterPlannedTimes(shiftActivityDTO.getStartDate(), shiftActivityDTO.getEndDate(), plannedTimeMap, plannedTimeId));
        }
    }

    private List<PlannedTime> filterPlannedTimes(Date startDate, Date endDate, Map<DateTimeInterval, PlannedTime> plannedTimeMap, BigInteger plannedTimeId) {
        DateTimeInterval activityInterval = new DateTimeInterval(startDate, endDate);
        plannedTimeMap = plannedTimeMap.entrySet().stream().filter(map -> map.getKey().overlaps(activityInterval)).collect(toMap(k -> k.getKey(), k -> k.getValue()));
        plannedTimeMap = plannedTimeMap.entrySet().stream().sorted(comparing(k -> k.getKey().getStartDate())).collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));
        List<PlannedTime> plannedTimes = new ArrayList<>();
        final boolean endDateInside = plannedTimeMap.entrySet().stream().anyMatch(k -> k.getKey().containsStartOrEnd(endDate));
        final boolean activityIntervalOverLapped = plannedTimeMap.entrySet().stream().anyMatch(k -> k.getKey().overlaps(activityInterval));

        if (!activityIntervalOverLapped) {
            plannedTimes.add(new PlannedTime(plannedTimeId, startDate, endDate));
        } else {

            if (plannedTimeMap.size() != 0) {
                DateTimeInterval lastInterval = plannedTimeMap.keySet().stream().skip(plannedTimeMap.keySet().size() - 1).findFirst().get();
                boolean addedAtLeading = false;
                for (Map.Entry<DateTimeInterval, PlannedTime> plannedTimeInterval : plannedTimeMap.entrySet()) {
                    DateTimeInterval shiftActivityInterVal = new DateTimeInterval(startDate, endDate);
                    if (plannedTimeInterval.getKey().containsInterval(shiftActivityInterVal)) {
                        plannedTimes.add(new PlannedTime(plannedTimeInterval.getValue().getPlannedTimeId(), startDate, endDate));
                        break;
                    } else if (startDate.before(plannedTimeInterval.getKey().getStartDate())) {
                        if (!addedAtLeading) {
                            plannedTimes.add(new PlannedTime(plannedTimeId, startDate, plannedTimeInterval.getKey().getStartDate()));
                            addedAtLeading = true;
                        }
                        plannedTimes.add(new PlannedTime(plannedTimeInterval.getValue().getPlannedTimeId(), plannedTimeInterval.getKey().getStartDate(), plannedTimeInterval.getKey().getEndDate()));
                        startDate = plannedTimeInterval.getKey().getEndDate();
                    } else if (startDate.equals(plannedTimeInterval.getKey().getStartDate())) {
                        plannedTimes.add(new PlannedTime(plannedTimeInterval.getValue().getPlannedTimeId(), startDate, plannedTimeInterval.getKey().getEndDate()));
                        startDate = plannedTimeInterval.getKey().getEndDate();
                    } else if (startDate.after(plannedTimeInterval.getKey().getStartDate())) {
                        plannedTimes.add(new PlannedTime(plannedTimeInterval.getValue().getPlannedTimeId(), startDate, plannedTimeInterval.getKey().getEndDate()));
                        startDate = plannedTimeInterval.getKey().getEndDate();
                    } else if (!plannedTimeInterval.getKey().overlaps(shiftActivityInterVal)) {
                        plannedTimes.add(new PlannedTime(plannedTimeId, startDate, endDate));
                    }
                }
                if (!endDateInside) {
                    plannedTimes.add(new PlannedTime(plannedTimeMap.get(lastInterval).getPlannedTimeId(), lastInterval.getEndDate(), endDate));
                }
            }
        }
        return plannedTimes;
    }
}
