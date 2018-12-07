package com.kairos.service.shift;

import com.kairos.commons.service.locale.LocaleService;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.dto.activity.shift.*;
import com.kairos.dto.user.country.time_slot.TimeSlotWrapper;
import com.kairos.dto.user.staff.unit_position.StaffUnitPositionUnitDataWrapper;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.break_settings.BreakSettings;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.break_settings.BreakSettingMongoRepository;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.ShiftValidatorService;
import com.kairos.wrapper.DateWiseShiftResponse;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstants.NO_CONFLICTS;
import static java.util.stream.Collectors.groupingBy;

/**
 * CreatedBy vipulpandey on 28/11/18
 **/
@Service
public class ShiftCopyService extends MongoBaseService {

    @Inject private ShiftMongoRepository shiftMongoRepository;
    @Inject private ExceptionService exceptionService;
    @Inject private ActivityMongoRepository activityRepository;
    @Inject private GenericIntegrationService genericIntegrationService;
    @Inject private BreakSettingMongoRepository breakSettingMongoRepository;
    @Inject private ShiftBreakService shiftBreakService;
    @Inject private ShiftValidatorService shiftValidatorService;
    @Inject private LocaleService localeService;
    @Inject private CostTimeAgreementRepository costTimeAgreementRepository;
    @Inject private ShiftService shiftService;

    public CopyShiftResponse copyShifts(Long unitId, CopyShiftDTO copyShiftDTO) {
        List<DateWiseShiftResponse> shifts = shiftMongoRepository.findAllByIdGroupByDate(copyShiftDTO.getShiftIds());
        if (!Optional.ofNullable(shifts).isPresent() || shifts.isEmpty()) {
            exceptionService.invalidOperationException("message.shift.notBlank");
        }
        Set<BigInteger> activityIds = shifts.stream().flatMap(s -> s.getShifts().stream().flatMap(ss -> ss.getActivities().stream().map(a -> a.getActivityId()))).collect(Collectors.toSet());
        List<ActivityWrapper> activities = activityRepository.findActivitiesAndTimeTypeByActivityId(new ArrayList<>(activityIds));
        Map<BigInteger, ActivityWrapper> activityMap = activities.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
        StaffUnitPositionUnitDataWrapper dataWrapper = genericIntegrationService.getStaffsUnitPosition(unitId, copyShiftDTO.getStaffIds(), copyShiftDTO.getExpertiseId());
        List<StaffUnitPositionDetails> staffDataList = dataWrapper.getStaffUnitPositionDetails();
        findAndAddCTAInUnitPositions(staffDataList,copyShiftDTO,dataWrapper);
        List<Long> expertiseIds=staffDataList.stream().map(staffUnitPositionDetails -> staffUnitPositionDetails.getExpertise().getId()).collect(Collectors.toList());
        List<BreakSettings> breakSettings = breakSettingMongoRepository.findAllByDeletedFalseAndExpertiseIdInOrderByCreatedAtAsc(expertiseIds);
        Map<BigInteger, ActivityWrapper> breakActivitiesMap = shiftBreakService.getBreakActivities(breakSettings,unitId);
        activityMap.putAll(breakActivitiesMap);
        Integer unCopiedShiftCount = 0;
        CopyShiftResponse copyShiftResponse = new CopyShiftResponse();

        for (Long currentStaffId : copyShiftDTO.getStaffIds()) {
            StaffUnitPositionDetails staffUnitPosition = staffDataList.parallelStream().filter(unitPosition -> unitPosition.getStaff().getId().equals(currentStaffId)).findFirst().get();
            // TODO PAVAN handle error
            Map<String, List<ShiftResponse>> response = copyForThisStaff(shifts, staffUnitPosition, activityMap, copyShiftDTO,breakActivitiesMap,dataWrapper.getTimeSlotWrappers(),breakSettings);
            StaffWiseShiftResponse successfullyCopied = new StaffWiseShiftResponse(staffUnitPosition.getStaff(), response.get("success"));
            StaffWiseShiftResponse errorInCopy = new StaffWiseShiftResponse(staffUnitPosition.getStaff(), response.get("error"));
            unCopiedShiftCount += response.get("error").size();
            copyShiftResponse.getSuccessFul().add(successfullyCopied);
            copyShiftResponse.getFailure().add(errorInCopy);
        }
        copyShiftResponse.setUnCopiedShiftCount(unCopiedShiftCount);
        return copyShiftResponse;
    }
    private void findAndAddCTAInUnitPositions(List<StaffUnitPositionDetails> staffDataList, CopyShiftDTO copyShiftDTO,StaffUnitPositionUnitDataWrapper dataWrapper){
        List<Long> unitPositionIds=staffDataList.stream().map(staffUnitPositionDetails -> staffUnitPositionDetails.getId()).collect(Collectors.toList());
        List<CTAResponseDTO> ctaResponseDTOS = costTimeAgreementRepository.getCTAByUnitPositionIdsAndDate(unitPositionIds, DateUtils.asDate( copyShiftDTO.getStartDate()),DateUtils.asDate(copyShiftDTO.getEndDate()));
        Map<Long,List<CTAResponseDTO>> CTAResponseMapByUnitPositionIds=ctaResponseDTOS.stream().collect(groupingBy(CTAResponseDTO::getUnitPositionId));
        staffDataList.stream().forEach(staffAdditionalInfoDTO -> {
            if(CTAResponseMapByUnitPositionIds.get(staffAdditionalInfoDTO.getId())!=null){
                List<CTAResponseDTO> ctaResponseDTOSList=CTAResponseMapByUnitPositionIds.get(staffAdditionalInfoDTO.getId());
                List<CTARuleTemplateDTO> ctaRuleTemplateDTOS=ctaResponseDTOSList.stream().flatMap(ctaResponseDTO -> ctaResponseDTO.getRuleTemplates().stream()).collect(Collectors.toList());
                staffAdditionalInfoDTO.setCtaRuleTemplates(ctaRuleTemplateDTOS);
                ShiftUtils.setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO,dataWrapper.getDayTypes(),dataWrapper.getPublicHoliday());
            }
        });
    }
    private Map<String, List<ShiftResponse>> copyForThisStaff(List<DateWiseShiftResponse> shifts, StaffUnitPositionDetails staffUnitPosition, Map<BigInteger, ActivityWrapper> activityMap, CopyShiftDTO copyShiftDTO,Map<BigInteger, ActivityWrapper> breakActivitiesMap,List<TimeSlotWrapper> timeSlots,List<BreakSettings> breakSettings ) {
        List<Shift> newShifts = new ArrayList<>(shifts.size());
        Map<String, List<ShiftResponse>> statusMap = new HashMap<>();
        List<ShiftResponse> successfullyCopiedShifts = new ArrayList<>();
        List<ShiftResponse> errorInCopyingShifts = new ArrayList<>();
        int counter = 0;
        LocalDate shiftCreationDate = copyShiftDTO.getStartDate();
        LocalDate shiftCreationLastDate = copyShiftDTO.getEndDate();
        ShiftResponse shiftResponse;
        while (shiftCreationLastDate.isAfter(shiftCreationDate) || shiftCreationLastDate.equals(shiftCreationDate)) {
            DateWiseShiftResponse dateWiseShiftResponse = shifts.get(counter);
            for (Shift sourceShift : dateWiseShiftResponse.getShifts()) {
                ShiftWithActivityDTO shiftWithActivityDTO = ObjectMapperUtils.copyPropertiesByMapper(sourceShift, ShiftWithActivityDTO.class);
                shiftWithActivityDTO.getActivities().forEach(s -> {
                    ActivityDTO activityDTO = ObjectMapperUtils.copyPropertiesByMapper(activityMap.get(s.getActivityId()).getActivity(), ActivityDTO.class);
                    s.setActivity(activityDTO);
                });
                List<String> validationMessages = shiftValidatorService.validateShiftWhileCopy(shiftWithActivityDTO, staffUnitPosition);
                shiftResponse = addShift(validationMessages, sourceShift, staffUnitPosition, shiftCreationDate, newShifts,breakActivitiesMap,activityMap,timeSlots,breakSettings);
                if (shiftResponse.isSuccess()) {
                    successfullyCopiedShifts.add(shiftResponse);
                } else {
                    errorInCopyingShifts.add(shiftResponse);
                }
            }
            shiftCreationDate = shiftCreationDate.plusDays(1L);
            if (counter++ == shifts.size() - 1) {
                counter = 0;
            }

        }
        statusMap.put("success", successfullyCopiedShifts);
        statusMap.put("error", errorInCopyingShifts);
        if (!newShifts.isEmpty()) {
            save(newShifts);
        }
        return statusMap;
    }
    private ShiftResponse addShift(List<String> responseMessages, Shift sourceShift, StaffUnitPositionDetails staffUnitPosition, LocalDate shiftCreationFirstDate, List<Shift> newShifts,Map<BigInteger, ActivityWrapper> breakActivitiesMap,Map<BigInteger, ActivityWrapper> activityMap ,List<TimeSlotWrapper> timeSlots,List<BreakSettings> breakSettings) {
        if (responseMessages.isEmpty()) {
            Shift copiedShift = new Shift(DateUtils.getDateByLocalDateAndLocalTime(shiftCreationFirstDate, DateUtils.asLocalTime(sourceShift.getStartDate())), DateUtils.getDateByLocalDateAndLocalTime(shiftCreationFirstDate, DateUtils.asLocalTime(sourceShift.getEndDate())),
                    sourceShift.getRemarks(), sourceShift.getActivities(), staffUnitPosition.getStaff().getId(), sourceShift.getUnitId(),
                    sourceShift.getScheduledMinutes(), sourceShift.getDurationMinutes(), sourceShift.getExternalId(), staffUnitPosition.getId(), sourceShift.getParentOpenShiftId(), sourceShift.getAllowedBreakDurationInMinute(), sourceShift.getId()
            ,sourceShift.getPhaseId(),sourceShift.getPlanningPeriodId());

            List<ShiftActivity> shiftActivities = shiftBreakService.addBreakInShiftsWhileCopy(activityMap, copiedShift, null,timeSlots,breakSettings);
            copiedShift.setActivities(shiftActivities);
            newShifts.add(copiedShift);
            return new ShiftResponse(sourceShift.getId(), sourceShift.getActivities().get(0).getActivityName(), Arrays.asList(NO_CONFLICTS), true, shiftCreationFirstDate);

        } else {
            List<String> errors = responseMessages.stream().map(responseMessage -> localeService.getMessage(responseMessage)).collect(Collectors.toList());
            return new ShiftResponse(sourceShift.getId(), sourceShift.getActivities().get(0).getActivityName(), errors, false, shiftCreationFirstDate);
        }
    }

}
