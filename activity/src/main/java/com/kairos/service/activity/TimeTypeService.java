package com.kairos.service.activity;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.AppConstants;
import com.kairos.constants.CommonConstants;
import com.kairos.dto.activity.activity.activity_tabs.*;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO;
import com.kairos.dto.user.country.day_type.DayType;
import com.kairos.dto.user.country.day_type.DayTypeEmploymentTypeWrapper;
import com.kairos.enums.OrganizationHierarchy;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.TimeTypes;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.model.activity.tabs.SkillActivityTab;
import com.kairos.persistence.model.activity.tabs.TimeCalculationActivityTab;
import com.kairos.persistence.model.activity.tabs.rules_activity_tab.RulesActivityTab;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.time_type.TimeTypeMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.wrapper.activity.ActivityTabsWrapper;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.enums.TimeTypeEnum.*;
import static com.kairos.service.activity.ActivityUtil.getCutoffInterval;

@Service
public class TimeTypeService extends MongoBaseService {
    public static final String PRESENCE = "Presence";
    public static final String ABSENCE = "Absence";
    @Inject
    private TimeTypeMongoRepository timeTypeMongoRepository;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private ActivityCategoryService activityCategoryService;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject private ShiftMongoRepository shiftMongoRepository;
    @Inject private ActivityService activityService;

    public List<TimeTypeDTO> createTimeType(List<TimeTypeDTO> timeTypeDTOs, Long countryId) {
        List<String> timeTypeLabels = timeTypeDTOs.stream().map(timeTypeDTO -> timeTypeDTO.getLabel()).collect(Collectors.toList());
        TimeType timeTypeResult = timeTypeMongoRepository.findByLabelsAndCountryId(timeTypeLabels, countryId);
        if (Optional.ofNullable(timeTypeResult).isPresent()) {
            exceptionService.duplicateDataException(MESSAGE_TIMETYPE_NAME_ALREADYEXIST);
        }
        BigInteger upperLevelTimeTypeId = timeTypeDTOs.get(0).getUpperLevelTimeTypeId();
        if (activityMongoRepository.existsByTimeTypeId(upperLevelTimeTypeId)) {
            exceptionService.actionNotPermittedException(ACTIVITY_ALREADY_EXISTS_TIME_TYPE);
        }
        TimeType upperTimeType = timeTypeMongoRepository.findOneById(upperLevelTimeTypeId);
        timeTypeDTOs.forEach(timeTypeDTO -> {
            TimeType timeType;
            if (timeTypeDTO.getTimeTypes() != null && timeTypeDTO.getUpperLevelTimeTypeId() != null) {
                timeType = new TimeType(TimeTypes.getByValue(timeTypeDTO.getTimeTypes()), timeTypeDTO.getLabel(), timeTypeDTO.getDescription(), timeTypeDTO.getBackgroundColor(), upperTimeType.getSecondLevelType(), countryId, timeTypeDTO.getActivityCanBeCopiedForOrganizationHierarchy());
                timeType.setCountryId(countryId);
                timeType.setUpperLevelTimeTypeId(timeTypeDTO.getUpperLevelTimeTypeId());
                timeType = save(timeType);
                if (timeTypeDTO.getUpperLevelTimeTypeId() != null) {
                    upperTimeType.getChildTimeTypeIds().add(timeType.getId());
                    upperTimeType.setLeafNode(false);
                    save(upperTimeType);
                }
                timeTypeDTO.setId(timeType.getId());
            }
        });
        return timeTypeDTOs;
    }

    public TimeTypeDTO updateTimeType(TimeTypeDTO timeTypeDTO, Long countryId) {

        boolean timeTypesExists = timeTypeMongoRepository.timeTypeAlreadyExistsByLabelAndCountryId(timeTypeDTO.getId(), timeTypeDTO.getLabel(), countryId);
        if (timeTypesExists) {
            exceptionService.duplicateDataException(MESSAGE_TIMETYPE_NAME_ALREADYEXIST);
        }

        TimeType timeType = timeTypeMongoRepository.findOneById(timeTypeDTO.getId());
        if (!Optional.ofNullable(timeType).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_TIMETYPE_NOTFOUND);
        }
        List<TimeType> timeTypes = new ArrayList<>();
        List<TimeType> childTimeTypes = timeTypeMongoRepository.findAllChildByParentId(timeType.getId(), countryId);
        Map<BigInteger, List<TimeType>> childTimeTypesMap = childTimeTypes.stream().collect(Collectors.groupingBy(t -> t.getUpperLevelTimeTypeId(), Collectors.toList()));
        List<BigInteger> childTimeTypeIds = childTimeTypes.stream().map(timetype -> timetype.getId()).collect(Collectors.toList());
        List<TimeType> leafTimeTypes = timeTypeMongoRepository.findAllChildTimeTypeByParentId(childTimeTypeIds);
        Map<BigInteger, List<TimeType>> leafTimeTypesMap = leafTimeTypes.stream().collect(Collectors.groupingBy(timetype -> timetype.getUpperLevelTimeTypeId(), Collectors.toList()));
        if (timeType.getUpperLevelTimeTypeId() == null && !timeType.getLabel().equalsIgnoreCase(timeTypeDTO.getLabel())) {
            //User Cannot Update NAME for TimeTypes of Second Level
            exceptionService.actionNotPermittedException(MESSAGE_TIMETYPE_RENAME_NOTALLOWED, timeType.getLabel());
        }
        timeType.setLabel(timeTypeDTO.getLabel());
        timeType.setDescription(timeTypeDTO.getDescription());
        if(!timeType.getBackgroundColor().equals(timeTypeDTO.getBackgroundColor())){
            updateColorInShift(timeTypeDTO, timeType);
            updateColorInActivity(timeTypeDTO, timeType);
        }
        timeType.setBackgroundColor(timeTypeDTO.getBackgroundColor());
        timeType.setPartOfTeam(timeTypeDTO.isPartOfTeam());
        timeType.setAllowedConflicts(timeTypeDTO.isAllowedConflicts());
        timeType.setAllowChildActivities(timeTypeDTO.isAllowChildActivities());
        timeType.setBreakNotHeldValid(timeTypeDTO.isBreakNotHeldValid());
        Set<OrganizationHierarchy> activityCanBeCopiedForOrganizationHierarchy = timeTypeDTO.getActivityCanBeCopiedForOrganizationHierarchy();
        if (isCollectionNotEmpty(activityCanBeCopiedForOrganizationHierarchy)) {
            if (activityCanBeCopiedForOrganizationHierarchy.size() == 1 && activityCanBeCopiedForOrganizationHierarchy.contains(OrganizationHierarchy.UNIT)) { //user cannot allow copy acitivity for Unit, without allowing copy activity for Organization
                exceptionService.actionNotPermittedException("message.timetype.copy.activity.withoutOrganization.notAllowed");
            }
            timeType.setActivityCanBeCopiedForOrganizationHierarchy(activityCanBeCopiedForOrganizationHierarchy);
        } else {
            timeType.setActivityCanBeCopiedForOrganizationHierarchy(Collections.EMPTY_SET);
        }
        List<TimeType> childTimeTypeList = childTimeTypesMap.get(timeTypeDTO.getId());
        if (Optional.ofNullable(childTimeTypeList).isPresent()) {
            setPropertiesInChildren(timeTypeDTO, timeType, timeTypes, leafTimeTypesMap, childTimeTypeList);
            timeTypes.addAll(childTimeTypeList);
        }
        timeTypes.add(timeType);
        if (timeType.isLeafNode()) {
            activityCategoryService.updateActivityCategoryForTimeType(countryId, timeType);
        }
        timeTypeMongoRepository.saveEntities(timeTypes);
        return timeTypeDTO;
    }

    private void updateColorInActivity(TimeTypeDTO timeTypeDTO, TimeType timeType) {
        List<Activity> activities = activityMongoRepository.findAllByTimeTypeId(timeType.getId());
        if (isCollectionNotEmpty(activities)) {
            activities.forEach(activity -> activity.getGeneralActivityTab().setBackgroundColor(timeTypeDTO.getBackgroundColor()));
            activityMongoRepository.saveEntities(activities);
        }
    }

    private void updateColorInShift(TimeTypeDTO timeTypeDTO, TimeType timeType) {
        Set<BigInteger> activitiyIds = activityMongoRepository.findAllByTimeTypeId(timeType.getId()).stream().map(activity -> activity.getId()).collect(Collectors.toSet());
        List<Shift> shifts = shiftMongoRepository.findShiftByShiftActivityIdAndBetweenDate(activitiyIds,null,null,null);
        shifts.forEach(shift -> shift.getActivities().forEach(shiftActivity -> {
            if(activitiyIds.contains(shiftActivity.getActivityId())){
                shiftActivity.setBackgroundColor(timeTypeDTO.getBackgroundColor());
            }
            shiftActivity.getChildActivities().forEach(childActivity -> {
                if(activitiyIds.contains(childActivity.getActivityId())){
                    childActivity.setBackgroundColor(timeTypeDTO.getBackgroundColor());
                }
            });
        }));
        if(isCollectionNotEmpty(shifts)){
            shiftMongoRepository.saveEntities(shifts);
        }
    }

    private void setPropertiesInChildren(TimeTypeDTO timeTypeDTO, TimeType timeType, List<TimeType> timeTypes, Map<BigInteger, List<TimeType>> leafTimeTypesMap, List<TimeType> childTimeTypeList) {
        boolean partOfTeamUpdated = false;
        boolean allowedChildActivityUpdated = false;
        boolean allowedConflictsUpdate = false;
        for (TimeType childTimeType : childTimeTypeList) {
            if (childTimeType.isPartOfTeam() != timeTypeDTO.isPartOfTeam() && childTimeType.getChildTimeTypeIds().isEmpty()) {
                childTimeType.setPartOfTeam(timeTypeDTO.isPartOfTeam());
                partOfTeamUpdated = true;
            }
            if (childTimeType.isAllowChildActivities() != timeTypeDTO.isAllowChildActivities() && childTimeType.getChildTimeTypeIds().isEmpty()) {
                childTimeType.setAllowChildActivities(timeTypeDTO.isAllowChildActivities());
                allowedChildActivityUpdated = true;
            }
            if (childTimeType.isAllowedConflicts() != timeTypeDTO.isAllowedConflicts() && childTimeType.getChildTimeTypeIds().isEmpty()) {
                childTimeType.setAllowedConflicts(timeTypeDTO.isAllowedConflicts());
                allowedConflictsUpdate = true;
            }

            childTimeType.setBackgroundColor(timeTypeDTO.getBackgroundColor());
            List<TimeType> leafTimeTypeList = leafTimeTypesMap.get(childTimeType.getId());
            if (Optional.ofNullable(leafTimeTypeList).isPresent()) {
                setPropertiesInLeafTimeTypes(timeTypeDTO, timeType, childTimeTypeList, partOfTeamUpdated, allowedChildActivityUpdated, allowedConflictsUpdate, childTimeType);
                timeTypes.addAll(leafTimeTypeList);
            }
        }
    }

    private void setPropertiesInLeafTimeTypes(TimeTypeDTO timeTypeDTO, TimeType timeType, List<TimeType> childTimeTypeList, boolean partOfTeamUpdated, boolean allowedChildActivityUpdated, boolean allowedConflictsUpdate, TimeType childTimeType) {
        for (TimeType leafTimeType : childTimeTypeList) {
            leafTimeType.setBackgroundColor(timeTypeDTO.getBackgroundColor());
            if (leafTimeType.isPartOfTeam() != timeTypeDTO.isPartOfTeam() && !partOfTeamUpdated && timeType.getUpperLevelTimeTypeId() != null) {
                childTimeType.setPartOfTeam(timeTypeDTO.isPartOfTeam());
            }
            if (leafTimeType.isAllowChildActivities() != timeTypeDTO.isAllowChildActivities() && !allowedChildActivityUpdated && timeType.getUpperLevelTimeTypeId() != null) {
                childTimeType.setAllowChildActivities(timeTypeDTO.isAllowChildActivities());
            }
            if (leafTimeType.isAllowedConflicts() != timeTypeDTO.isAllowedConflicts() && !allowedConflictsUpdate && timeType.getUpperLevelTimeTypeId() != null) {
                childTimeType.setAllowedConflicts(timeTypeDTO.isAllowedConflicts());
            }
        }
    }

    public List<TimeTypeDTO> getAllTimeType(BigInteger timeTypeId, Long countryId) {
        List<TimeType> topLevelTimeTypes = timeTypeMongoRepository.getTopLevelTimeType(countryId);
        List<TimeTypeDTO> timeTypeDTOS = new ArrayList<>(2);
        TimeTypeDTO workingTimeTypeDTO = new TimeTypeDTO(TimeTypes.WORKING_TYPE.toValue(), AppConstants.WORKING_TYPE_COLOR);
        TimeTypeDTO nonWorkingTimeTypeDTO = new TimeTypeDTO(TimeTypes.NON_WORKING_TYPE.toValue(), AppConstants.NON_WORKING_TYPE_COLOR);
        if (topLevelTimeTypes.isEmpty()) {
            timeTypeDTOS.add(workingTimeTypeDTO);
            timeTypeDTOS.add(nonWorkingTimeTypeDTO);
            return timeTypeDTOS;
        }
        List<TimeType> timeTypes = timeTypeMongoRepository.findAllLowerLevelTimeType(countryId);
        List<TimeTypeDTO> parentOfWorkingTimeType = new ArrayList<>();
        List<TimeTypeDTO> parentOfNonWorkingTimeType = new ArrayList<>();
        for (TimeType timeType : topLevelTimeTypes) {
            TimeTypeDTO timeTypeDTO = new TimeTypeDTO(timeType.getId(), timeType.getTimeTypes().toValue(), timeType.getLabel(), timeType.getDescription(), timeType.getBackgroundColor(), timeType.getActivityCanBeCopiedForOrganizationHierarchy(), timeType.isPartOfTeam(), timeType.isAllowChildActivities(),timeType.isAllowedConflicts(),timeType.isBreakNotHeldValid());
            timeTypeDTO.setSecondLevelType(timeType.getSecondLevelType());
            if ( timeType.getId().equals(timeTypeId)) {
                timeTypeDTO.setSelected(true);
            }
            timeTypeDTO.setTimeTypes(timeType.getTimeTypes().toValue());
            timeTypeDTO.setChildren(getLowerLevelTimeTypeDTOs(timeTypeId, timeType.getId(), timeTypes));
            if (timeType.getTimeTypes().equals(TimeTypes.WORKING_TYPE)) {
                parentOfWorkingTimeType.add(timeTypeDTO);
            } else {
                parentOfNonWorkingTimeType.add(timeTypeDTO);
            }
        }
        workingTimeTypeDTO.setChildren(parentOfWorkingTimeType);
        nonWorkingTimeTypeDTO.setChildren(parentOfNonWorkingTimeType);
        timeTypeDTOS.add(workingTimeTypeDTO);
        timeTypeDTOS.add(nonWorkingTimeTypeDTO);
        return timeTypeDTOS;
    }

    public Map<String, List<TimeType>> getPresenceAbsenceTimeType(Long countryId) {
        List<TimeType> timeTypes = timeTypeMongoRepository.findAllTimeTypeByCountryId(countryId);
        Map<BigInteger, List<TimeType>> timeTypeMap = timeTypes.stream().filter(t -> t.getUpperLevelTimeTypeId() != null).collect(Collectors.groupingBy(TimeType::getUpperLevelTimeTypeId, Collectors.toList()));
        Map<String, List<TimeType>> presenceAbsenceTimeTypeMap = new HashMap<>();
        timeTypes.forEach(t -> {
            if (t.getLabel().equals(PRESENCE)) {
                List<TimeType> presenceTimeTypes = getChildOfTimeType(t, timeTypeMap);
                presenceTimeTypes.add(t);
                presenceAbsenceTimeTypeMap.put(PRESENCE, presenceTimeTypes);
            } else if (t.getLabel().equals(ABSENCE)) {
                List<TimeType> absenceTimeTypes = getChildOfTimeType(t, timeTypeMap);
                absenceTimeTypes.add(t);
                presenceAbsenceTimeTypeMap.put(ABSENCE, absenceTimeTypes);
            }
        });
        return presenceAbsenceTimeTypeMap;
    }

    private List<TimeType> getChildOfTimeType(TimeType timeType, Map<BigInteger, List<TimeType>> timeTypeMap) {
        List<TimeType> timeTypes = new ArrayList<>();
        List<TimeType> secondLevelTimeTypes = null;
        secondLevelTimeTypes = timeTypeMap.get(timeType.getId());
        timeTypes.addAll(secondLevelTimeTypes);
        if (secondLevelTimeTypes != null) {
            secondLevelTimeTypes.forEach(t -> {
                List<TimeType> leaftimeTypes = timeTypeMap.get(t.getId());
                if (leaftimeTypes != null) {
                    timeTypes.addAll(leaftimeTypes);
                }
            });
        }
        return timeTypes;
    }

    public List<TimeTypeDTO> getAllTimeTypeByCountryId(Long countryId) {
        List<TimeType> timeTypes = timeTypeMongoRepository.findAllTimeTypeByCountryId(countryId);
        List<TimeTypeDTO> timeTypeDTOS = new ArrayList<>(timeTypes.size());
        timeTypes.forEach(t -> {
            TimeTypeDTO timeTypeDTO = new TimeTypeDTO(t.getId(), t.getTimeTypes().toValue(), t.getUpperLevelTimeTypeId());
            timeTypeDTO.setLabel(t.getLabel());
            timeTypeDTOS.add(timeTypeDTO);
        });
        return timeTypeDTOS;
    }

    private List<TimeTypeDTO> getLowerLevelTimeTypeDTOs(BigInteger timeTypeId, BigInteger upperlevelTimeTypeId, List<TimeType> timeTypes) {
        List<TimeTypeDTO> lowerLevelTimeTypeDTOS = new ArrayList<>();
        timeTypes.forEach(timeType -> {
            if (timeType.getUpperLevelTimeTypeId().equals(upperlevelTimeTypeId)) {
                TimeTypeDTO levelTwoTimeTypeDTO = new TimeTypeDTO(timeType.getId(), timeType.getTimeTypes().toValue(), timeType.getLabel(), timeType.getDescription(), timeType.getBackgroundColor(), timeType.getActivityCanBeCopiedForOrganizationHierarchy(), timeType.isPartOfTeam(), timeType.isAllowChildActivities(),timeType.isAllowedConflicts(),timeType.isBreakNotHeldValid());
                if (timeTypeId != null && timeType.getId().equals(timeTypeId)) {
                    levelTwoTimeTypeDTO.setSelected(true);
                }
                levelTwoTimeTypeDTO.setSecondLevelType(timeType.getSecondLevelType());
                levelTwoTimeTypeDTO.setTimeTypes(timeType.getTimeTypes().toValue());
                levelTwoTimeTypeDTO.setChildren(getLowerLevelTimeTypeDTOs(timeTypeId, timeType.getId(), timeTypes));
                levelTwoTimeTypeDTO.setUpperLevelTimeTypeId(upperlevelTimeTypeId);
                lowerLevelTimeTypeDTOS.add(levelTwoTimeTypeDTO);
            }
        });
        return lowerLevelTimeTypeDTOS;
    }

    public boolean deleteTimeType(BigInteger timeTypeId, Long countryId) {
        List<Activity> activity = activityMongoRepository.findAllByTimeTypeId(timeTypeId);
        List<TimeType> timeTypes = timeTypeMongoRepository.findAllChildByParentId(timeTypeId, countryId);
        boolean reasonCodeExists = userIntegrationService.isReasonCodeLinkedToTimeType(countryId, timeTypeId);
        if (reasonCodeExists) {
            exceptionService.actionNotPermittedException(MESSAGE_TIMETYPE_LINKED_REASON_CODE);
        }
        if (activity.isEmpty() && timeTypes.isEmpty()) {
            TimeType timeType = timeTypeMongoRepository.findOne(timeTypeId);
            if (timeType != null && timeType.getUpperLevelTimeTypeId() == null) {
                //User Cannot Delete TimeType of Second Level
                exceptionService.actionNotPermittedException(MESSAGE_TIMETYPE_DELETION_NOTALLOWED, timeType.getLabel());
            } else {
                activityCategoryService.removeTimeTypeRelatedCategory(countryId, timeTypeId);
                timeType.setDeleted(true);
                save(timeType);
            }
        } else exceptionService.timeTypeLinkedException(MESSAGE_TIMETYPE_LINKED);
        return true;
    }


    public Boolean createDefaultTimeTypes(Long countryId) {
        List<TimeType> allTimeTypes = new ArrayList<>();
        List<TimeType> workingTimeTypes = new ArrayList<>();
        TimeType presenceTimeType = new TimeType(TimeTypes.WORKING_TYPE, PRESENCE, "", AppConstants.WORKING_TYPE_COLOR, TimeTypeEnum.PRESENCE, countryId, Collections.EMPTY_SET);
        TimeType absenceTimeType = new TimeType(TimeTypes.WORKING_TYPE, ABSENCE, "", AppConstants.WORKING_TYPE_COLOR, TimeTypeEnum.ABSENCE, countryId, Collections.EMPTY_SET);
        TimeType breakTimeType = new TimeType(TimeTypes.WORKING_TYPE, "Paid Break", "", AppConstants.WORKING_TYPE_COLOR, PAID_BREAK, countryId, Collections.EMPTY_SET);
        workingTimeTypes.add(presenceTimeType);
        workingTimeTypes.add(absenceTimeType);
        workingTimeTypes.add(breakTimeType);

        List<TimeType> nonWorkingTimeTypes = new ArrayList<>();
        TimeType volunteerTimeType = new TimeType(TimeTypes.NON_WORKING_TYPE, "Volunteer Time", "", AppConstants.NON_WORKING_TYPE_COLOR, VOLUNTEER, countryId, Collections.EMPTY_SET);
        TimeType timeBankOffTimeType = new TimeType(TimeTypes.NON_WORKING_TYPE, "Timebank Off Time", "", AppConstants.NON_WORKING_TYPE_COLOR, TIME_BANK, countryId, Collections.EMPTY_SET);
        TimeType unPaidBreakTimeType = new TimeType(TimeTypes.NON_WORKING_TYPE, "Unpaid Break", "", AppConstants.NON_WORKING_TYPE_COLOR, UNPAID_BREAK, countryId, Collections.EMPTY_SET);
        TimeType timeSplitInShiftTimeType = new TimeType(TimeTypes.NON_WORKING_TYPE, "Time between Split Shifts", "", AppConstants.NON_WORKING_TYPE_COLOR, SHIFT_SPLIT_TIME, countryId, Collections.EMPTY_SET);
        TimeType dutyFreeTimeType = new TimeType(TimeTypes.NON_WORKING_TYPE, "Duty-free, Self-Paid", "", AppConstants.NON_WORKING_TYPE_COLOR, SELF_PAID, countryId, Collections.EMPTY_SET);
        TimeType sicknessTimeType = new TimeType(TimeTypes.NON_WORKING_TYPE, "Planned Sickness on Freedays", "", AppConstants.NON_WORKING_TYPE_COLOR, PLANNED_SICK_ON_FREE_DAYS, countryId, Collections.EMPTY_SET);
        TimeType unavailableTimeType = new TimeType(TimeTypes.NON_WORKING_TYPE, "Unavailable Time", "", AppConstants.NON_WORKING_TYPE_COLOR, UNAVAILABLE_TIME, countryId, Collections.EMPTY_SET);
        TimeType restingTimeType = new TimeType(TimeTypes.NON_WORKING_TYPE, "Resting Time", "", AppConstants.NON_WORKING_TYPE_COLOR, RESTING_TIME, countryId, Collections.EMPTY_SET);
        TimeType vetoTimeType = new TimeType(TimeTypes.NON_WORKING_TYPE, "Veto", "", AppConstants.NON_WORKING_TYPE_COLOR, VETO, countryId, Collections.EMPTY_SET);
        TimeType stopBrickTimeType = new TimeType(TimeTypes.NON_WORKING_TYPE, "Stopbrick", "", AppConstants.NON_WORKING_TYPE_COLOR, STOP_BRICK, countryId, Collections.EMPTY_SET);
        TimeType availableTimeType = new TimeType(TimeTypes.NON_WORKING_TYPE, "Available Time", "", AppConstants.NON_WORKING_TYPE_COLOR, AVAILABLE_TIME, countryId, Collections.EMPTY_SET);
        TimeType protectedDaysOff = new TimeType(TimeTypes.NON_WORKING_TYPE, "Protected Days off", "", AppConstants.NON_WORKING_TYPE_COLOR, PROTECTED_DAYS_OFF, countryId, Collections.EMPTY_SET);
        nonWorkingTimeTypes.add(volunteerTimeType);
        nonWorkingTimeTypes.add(timeBankOffTimeType);
        nonWorkingTimeTypes.add(unPaidBreakTimeType);
        nonWorkingTimeTypes.add(timeSplitInShiftTimeType);
        nonWorkingTimeTypes.add(dutyFreeTimeType);
        nonWorkingTimeTypes.add(sicknessTimeType);
        nonWorkingTimeTypes.add(unavailableTimeType);
        nonWorkingTimeTypes.add(restingTimeType);
        nonWorkingTimeTypes.add(vetoTimeType);
        nonWorkingTimeTypes.add(stopBrickTimeType);
        nonWorkingTimeTypes.add(availableTimeType);
        nonWorkingTimeTypes.add(protectedDaysOff);
        allTimeTypes.addAll(workingTimeTypes);
        allTimeTypes.addAll(nonWorkingTimeTypes);

        timeTypeMongoRepository.saveEntities(allTimeTypes);

        return true;
    }


    public List<TimeType> getAllTimeTypesByCountryId(Long countryId) {
        return timeTypeMongoRepository.findAllTimeTypeByCountryId(countryId);
    }


    public Boolean existsByIdAndCountryId(BigInteger id, Long countryId) {
        return timeTypeMongoRepository.existsByIdAndCountryIdAndDeletedFalse(id, countryId);
    }

    public List<BigInteger> getAllTimeTypeWithItsLowerLevel(Long countryId, List<BigInteger> timeTypeIds){
        List<TimeTypeDTO> timeTypeDTOS =  getAllTimeType(null,countryId);
        List<BigInteger> resultTimeTypeDTOS = new ArrayList<>();
        updateTimeTypeList(resultTimeTypeDTOS,timeTypeDTOS.get(0).getChildren(), timeTypeIds,false);
        updateTimeTypeList(resultTimeTypeDTOS,timeTypeDTOS.get(1).getChildren(), timeTypeIds,false);
        return resultTimeTypeDTOS;
    }

    private void updateTimeTypeList(List<BigInteger> resultTimeTypeDTOS, List<TimeTypeDTO> timeTypeDTOS, List<BigInteger> timeTypeIds, boolean addAllLowerLevelChildren){
        for(TimeTypeDTO timeTypeDTO : timeTypeDTOS) {
            if(timeTypeIds.indexOf(timeTypeDTO.getId())>=0){
                resultTimeTypeDTOS.add(timeTypeDTO.getId());
                if(isCollectionNotEmpty(timeTypeDTO.getChildren())){
                    updateTimeTypeList(resultTimeTypeDTOS,timeTypeDTO.getChildren(), timeTypeIds,true);
                }
            }else if(addAllLowerLevelChildren){
                if(isCollectionNotEmpty(timeTypeDTO.getChildren())) {
                    updateTimeTypeList(resultTimeTypeDTOS, timeTypeDTO.getChildren(), timeTypeIds, true);
                }else{
                    resultTimeTypeDTOS.add(timeTypeDTO.getId());
                }
            }else{
                updateTimeTypeList(resultTimeTypeDTOS, timeTypeDTO.getChildren(), timeTypeIds, false);
            }
        }
    }

    public TimeCalculationActivityDTO updateTimeCalculationTabOfTimeType(TimeCalculationActivityDTO timeCalculationActivityDTO, BigInteger timeTypeId) {
        TimeCalculationActivityTab timeCalculationActivityTab = new TimeCalculationActivityTab();
        ObjectMapperUtils.copyProperties(timeCalculationActivityDTO, timeCalculationActivityTab);
        TimeType timeType = timeTypeMongoRepository.findOne(timeTypeId);
        if (!Optional.ofNullable(timeType).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_TIMETYPE_NOTFOUND, timeTypeId);
        }
        //timeCalculationActivityDTO = verifyAndDeleteCompositeActivity(timeCalculationActivityDTO, availableAllowActivity);
        if (!timeCalculationActivityDTO.isAvailableAllowActivity()) {
            timeType.setTimeCalculationActivityTab(timeCalculationActivityTab);
            if (!timeCalculationActivityTab.getMethodForCalculatingTime().equals(CommonConstants.FULL_WEEK)) {
                timeCalculationActivityTab.setDayTypes(timeType.getRulesActivityTab().getDayTypes());
            }
            timeTypeMongoRepository.save(timeType);
        }
        return timeCalculationActivityDTO;
    }

    public ActivityTabsWrapper getTimeCalculationTabOfTimeType(BigInteger timeTypeId, Long countryId) {
        List<DayType> dayTypes = userIntegrationService.getDayTypesByCountryId(countryId);
        TimeType timeType = timeTypeMongoRepository.findOne(timeTypeId);
        TimeCalculationActivityTab timeCalculationActivityTab = timeType.getTimeCalculationActivityTab();
        List<Long> rulesTabDayTypes = timeType.getRulesActivityTab().getDayTypes();
        return new ActivityTabsWrapper(timeCalculationActivityTab, dayTypes, rulesTabDayTypes);
    }

    public ActivityTabsWrapper updateRulesTab(RulesActivityTabDTO rulesActivityDTO,BigInteger timeTypeId) {
        activityService.validateActivityTimeRules(rulesActivityDTO.getEarliestStartTime(), rulesActivityDTO.getLatestStartTime(), rulesActivityDTO.getMaximumEndTime(), rulesActivityDTO.getShortestTime(), rulesActivityDTO.getLongestTime());
        RulesActivityTab rulesActivityTab = ObjectMapperUtils.copyPropertiesByMapper(rulesActivityDTO, RulesActivityTab.class);
        TimeType timeType = timeTypeMongoRepository.findOne(timeTypeId);
        if (!Optional.ofNullable(timeType).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_TIMETYPE_NOTFOUND, timeTypeId);
        }
        if (rulesActivityDTO.getCutOffIntervalUnit() != null && rulesActivityDTO.getCutOffStartFrom() != null) {
            if (CutOffIntervalUnit.DAYS.equals(rulesActivityDTO.getCutOffIntervalUnit()) && rulesActivityDTO.getCutOffdayValue() == 0) {
                exceptionService.invalidRequestException(ERROR_DAYVALUE_ZERO);
            }
            List<CutOffInterval> cutOffIntervals = getCutoffInterval(rulesActivityDTO.getCutOffStartFrom(), rulesActivityDTO.getCutOffIntervalUnit(), rulesActivityDTO.getCutOffdayValue());
            rulesActivityTab.setCutOffIntervals(cutOffIntervals);
            rulesActivityDTO.setCutOffIntervals(cutOffIntervals);
        }
        timeType.setRulesActivityTab(rulesActivityTab);
        if (!timeType.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(CommonConstants.FULL_WEEK)) {
            timeType.getTimeCalculationActivityTab().setDayTypes(timeType.getRulesActivityTab().getDayTypes());
        }
        timeTypeMongoRepository.save(timeType);
        return new ActivityTabsWrapper(rulesActivityTab);
    }

    public ActivityTabsWrapper getPhaseSettingTabOfTimeType(BigInteger timeTypeId, Long countryId) {
        TimeType timeType = timeTypeMongoRepository.findOne(timeTypeId);
        if (!Optional.ofNullable(timeType).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_TIMETYPE_NOTFOUND, timeTypeId);
        }
        DayTypeEmploymentTypeWrapper dayTypeEmploymentTypeWrapper = userIntegrationService.getDayTypesAndEmploymentTypes(countryId);
        List<DayType> dayTypes = dayTypeEmploymentTypeWrapper.getDayTypes();
        List<EmploymentTypeDTO> employmentTypeDTOS = dayTypeEmploymentTypeWrapper.getEmploymentTypes();
        Set<AccessGroupRole> roles = AccessGroupRole.getAllRoles();
        PhaseSettingsActivityTab phaseSettingsActivityTab = timeType.getPhaseSettingsActivityTab();
        return new ActivityTabsWrapper(roles, phaseSettingsActivityTab, dayTypes, employmentTypeDTOS);
    }

    public PhaseSettingsActivityTab updatePhaseSettingTab(PhaseSettingsActivityTab phaseSettingsActivityTab,BigInteger timeTypeId) {
        TimeType timeType = timeTypeMongoRepository.findOne(timeTypeId);
        if (!Optional.ofNullable(timeType).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_TIMETYPE_NOTFOUND, phaseSettingsActivityTab.getActivityId());
        }
        timeType.setPhaseSettingsActivityTab(phaseSettingsActivityTab);
        timeTypeMongoRepository.save(timeType);
        return phaseSettingsActivityTab;
    }

    public ActivityTabsWrapper getRulesTabOfTimeType(BigInteger timeTypeId, Long countryId) {
        DayTypeEmploymentTypeWrapper dayTypeEmploymentTypeWrapper = userIntegrationService.getDayTypesAndEmploymentTypes(countryId);
        List<DayType> dayTypes = dayTypeEmploymentTypeWrapper.getDayTypes();
        List<EmploymentTypeDTO> employmentTypeDTOS = dayTypeEmploymentTypeWrapper.getEmploymentTypes();
        TimeType timeType = timeTypeMongoRepository.findOne(timeTypeId);
        RulesActivityTab rulesActivityTab = timeType.getRulesActivityTab();
        return new ActivityTabsWrapper(rulesActivityTab, dayTypes, employmentTypeDTOS);
    }


    public ActivityTabsWrapper updateSkillTabOfTimeType(SkillActivityDTO skillActivityDTO,BigInteger timeTypeId) {
        TimeType timeType = timeTypeMongoRepository.findOne(timeTypeId);
        if (!Optional.ofNullable(timeType).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_TIMETYPE_NOTFOUND, skillActivityDTO.getActivityId());
        }
        SkillActivityTab skillActivityTab = new SkillActivityTab(skillActivityDTO.getActivitySkills());
        timeType.setSkillActivityTab(skillActivityTab);
        timeTypeMongoRepository.save(timeType);
        return new ActivityTabsWrapper(skillActivityTab);
    }

    public ActivityTabsWrapper getSkillTabOfTimeType(BigInteger timeTypeId) {
        TimeType timeType = timeTypeMongoRepository.findOne(timeTypeId);
        return new ActivityTabsWrapper(timeType.getSkillActivityTab());
    }

    public void updateOrgMappingDetailOfActivity(OrganizationMappingDTO organizationMappingDTO, BigInteger timeTypeId) {
        TimeType timeType = timeTypeMongoRepository.findOne(timeTypeId);
        if (!Optional.ofNullable(timeType).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_TIMETYPE_NOTFOUND, timeTypeId);
        }
        boolean isSuccess = userIntegrationService.verifyOrganizationExpertizeAndRegions(organizationMappingDTO);
        if (!isSuccess) {
            exceptionService.dataNotFoundException(MESSAGE_PARAMETERS_INCORRECT);
        }
        timeType.setRegions(organizationMappingDTO.getRegions());
        timeType.setExpertises(organizationMappingDTO.getExpertises());
        timeType.setOrganizationSubTypes(organizationMappingDTO.getOrganizationSubTypes());
        timeType.setOrganizationTypes(organizationMappingDTO.getOrganizationTypes());
        timeType.setLevels(organizationMappingDTO.getLevel());
        timeType.setEmploymentTypes(organizationMappingDTO.getEmploymentTypes());
        timeTypeMongoRepository.save(timeType);
    }

    public OrganizationMappingDTO getOrgMappingDetailOfTimeType(BigInteger timeTypeId) {
        TimeType timeType = timeTypeMongoRepository.findOne(timeTypeId);
        if (!Optional.ofNullable(timeType).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_TIMETYPE_NOTFOUND, timeTypeId);
        }
        OrganizationMappingDTO organizationMappingDTO = new OrganizationMappingDTO();
        organizationMappingDTO.setOrganizationSubTypes(timeType.getOrganizationSubTypes());
        organizationMappingDTO.setExpertises(timeType.getExpertises());
        organizationMappingDTO.setRegions(timeType.getRegions());
        organizationMappingDTO.setLevel(timeType.getLevels());
        organizationMappingDTO.setOrganizationTypes(timeType.getOrganizationTypes());
        organizationMappingDTO.setEmploymentTypes(timeType.getEmploymentTypes());
        return organizationMappingDTO;

    }

}
