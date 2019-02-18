package com.kairos.service.activity;


import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.enums.Hierarchy;
import com.kairos.enums.TimeTypes;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.time_type.TimeTypeMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.enums.TimeTypeEnum.*;

@Service
public class TimeTypeService extends MongoBaseService {
    @Inject
    private TimeTypeMongoRepository timeTypeMongoRepository;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private ActivityCategoryService activityCategoryService;

    public List<TimeTypeDTO> createTimeType(List<TimeTypeDTO> timeTypeDTOs, Long countryId) {
        List<String> timeTypeLabels = timeTypeDTOs.stream().map(timeTypeDTO -> timeTypeDTO.getLabel()).collect(Collectors.toList());
        TimeType timeTypeResult = timeTypeMongoRepository.findByLabelsAndCountryId(timeTypeLabels, countryId);
        if (Optional.ofNullable(timeTypeResult).isPresent()) {
            exceptionService.duplicateDataException("message.timetype.name.alreadyexist");
        }
        BigInteger upperLevelTimeTypeId = timeTypeDTOs.get(0).getUpperLevelTimeTypeId();
        if (activityMongoRepository.existsByTimeTypeId(upperLevelTimeTypeId)) {
            exceptionService.actionNotPermittedException("activity.already.exists.time_type");
        }
        TimeType upperTimeType = timeTypeMongoRepository.findOneById(upperLevelTimeTypeId);
        timeTypeDTOs.forEach(timeTypeDTO -> {
            TimeType timeType;
            if (timeTypeDTO.getTimeTypes() != null && timeTypeDTO.getUpperLevelTimeTypeId() != null) {
                timeType = new TimeType(TimeTypes.getByValue(timeTypeDTO.getTimeTypes()), timeTypeDTO.getLabel(), timeTypeDTO.getDescription(), timeTypeDTO.getBackgroundColor(), upperTimeType.getSecondLevelType(), countryId, timeTypeDTO.getAcitivityCanBeCopiedForHierarchy());
                timeType.setCountryId(countryId);
                //if (timeTypeDTO.getUpperLevelTimeTypeId() != null) {
                timeType.setUpperLevelTimeTypeId(timeTypeDTO.getUpperLevelTimeTypeId());
                //}
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
            exceptionService.duplicateDataException("message.timetype.name.alreadyexist");
        }

        TimeType timeType = timeTypeMongoRepository.findOneById(timeTypeDTO.getId());
        if (Optional.ofNullable(timeType).isPresent()) {
            List<TimeType> timeTypes = new ArrayList<>();
            List<TimeType> childTimeTypes = timeTypeMongoRepository.findAllChildByParentId(timeType.getId(), countryId);
            Map<BigInteger, List<TimeType>> childTimeTypesMap = childTimeTypes.stream().collect(Collectors.groupingBy(t -> t.getUpperLevelTimeTypeId(), Collectors.toList()));
            List<BigInteger> childTimeTypeIds = childTimeTypes.stream().map(timetype -> timetype.getId()).collect(Collectors.toList());
            List<TimeType> leafTimeTypes = timeTypeMongoRepository.findAllChildTimeTypeByParentId(childTimeTypeIds);
            Map<BigInteger, List<TimeType>> leafTimeTypesMap = leafTimeTypes.stream().collect(Collectors.groupingBy(timetype -> timetype.getUpperLevelTimeTypeId(), Collectors.toList()));

            if (timeType.getUpperLevelTimeTypeId() == null && !timeType.getLabel().equalsIgnoreCase(timeTypeDTO.getLabel())) {
                //User Cannot Update NAME for TimeTypes of Second Level
                exceptionService.actionNotPermittedException("message.timetype.rename.notAllowed", timeType.getLabel());
            }
            timeType.setLabel(timeTypeDTO.getLabel());
            timeType.setDescription(timeTypeDTO.getDescription());
            timeType.setBackgroundColor(timeTypeDTO.getBackgroundColor());

            List<Hierarchy> acitivityCanBeCopiedForHierarchy = timeTypeDTO.getAcitivityCanBeCopiedForHierarchy();
            if (isCollectionNotEmpty(acitivityCanBeCopiedForHierarchy)) {
                if (acitivityCanBeCopiedForHierarchy.size() == 1 && acitivityCanBeCopiedForHierarchy.contains(Hierarchy.UNIT)) { //user cannot allow copy acitivity for Unit, without allowing copy activity for Organization
                    exceptionService.actionNotPermittedException("message.timetype.copy.activity.withoutOrganization.notAllowed");
                }
                timeType.setAcitivityCanBeCopiedForHierarchy(acitivityCanBeCopiedForHierarchy);
            }
            List<TimeType> childTimeTypeList = childTimeTypesMap.get(timeTypeDTO.getId());
            if (Optional.ofNullable(childTimeTypeList).isPresent()) {
                childTimeTypeList.forEach(childTimeType -> {
                    childTimeType.setBackgroundColor(timeTypeDTO.getBackgroundColor());
                    List<TimeType> leafTimeTypeList = leafTimeTypesMap.get(childTimeType.getId());
                    if (Optional.ofNullable(leafTimeTypeList).isPresent()) {
                        leafTimeTypeList.forEach(leafTimeType -> {
                            leafTimeType.setBackgroundColor(timeTypeDTO.getBackgroundColor());
                        });
                        timeTypes.addAll(leafTimeTypeList);
                    }
                });
                timeTypes.addAll(childTimeTypeList);
            }
            timeTypes.add(timeType);
            if (timeType.isLeafNode()) {
                activityCategoryService.updateActivityCategoryForTimeType(countryId, timeType);
            }
            save(timeTypes);
        } else {
            exceptionService.dataNotFoundByIdException("message.timetype.notfound");
        }
        return timeTypeDTO;
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
            TimeTypeDTO timeTypeDTO = new TimeTypeDTO(timeType.getId(), timeType.getTimeTypes().toValue(), timeType.getLabel(), timeType.getDescription(), timeType.getBackgroundColor(), timeType.getAcitivityCanBeCopiedForHierarchy());
            timeTypeDTO.setSecondLevelType(timeType.getSecondLevelType());
            if (timeTypeId != null && timeType.getId().equals(timeTypeId)) {
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
        List<TimeType> timeTypes = timeTypeMongoRepository.findAllByCountryId(countryId);
        Map<BigInteger, List<TimeType>> timeTypeMap = timeTypes.stream().filter(t -> t.getUpperLevelTimeTypeId() != null).collect(Collectors.groupingBy(TimeType::getUpperLevelTimeTypeId, Collectors.toList()));
        Map<String, List<TimeType>> presenceAbsenceTimeTypeMap = new HashMap<>();
        timeTypes.forEach(t -> {
            if (t.getLabel().equals("Presence")) {
                List<TimeType> presenceTimeTypes = getChildOfTimeType(t, timeTypeMap);
                presenceTimeTypes.add(t);
                presenceAbsenceTimeTypeMap.put("Presence", presenceTimeTypes);
            } else if (t.getLabel().equals("Absence")) {
                List<TimeType> absenceTimeTypes = getChildOfTimeType(t, timeTypeMap);
                absenceTimeTypes.add(t);
                presenceAbsenceTimeTypeMap.put("Absence", absenceTimeTypes);
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
        List<TimeType> timeTypes = timeTypeMongoRepository.findAllByCountryId(countryId);
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
                TimeTypeDTO levelTwoTimeTypeDTO = new TimeTypeDTO(timeType.getId(), timeType.getTimeTypes().toValue(), timeType.getLabel(), timeType.getDescription(), timeType.getBackgroundColor(), timeType.getAcitivityCanBeCopiedForHierarchy());
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
        if (activity.isEmpty() && timeTypes.isEmpty()) {
            TimeType timeType = timeTypeMongoRepository.findOne(timeTypeId);
            if (timeType != null && timeType.getUpperLevelTimeTypeId() == null) {
                //User Cannot Delete TimeType of Second Level
                exceptionService.actionNotPermittedException("message.timetype.deletion.notAllowed", timeType.getLabel());
            } else {
                activityCategoryService.removeTimeTypeRelatedCategory(countryId, timeTypeId);
                timeType.setDeleted(true);
                save(timeType);
            }
        } else exceptionService.timeTypeLinkedException("message.timetype.linked");
        return true;
    }


    public Boolean createDefaultTimeTypes(Long countryId) {
        List<TimeType> allTimeTypes = new ArrayList<>();
        List<TimeType> workingTimeTypes = new ArrayList<>();
        TimeType presenceTimeType = new TimeType(TimeTypes.WORKING_TYPE, "Presence", "", AppConstants.WORKING_TYPE_COLOR, PRESENCE, countryId, Collections.EMPTY_LIST);
        TimeType absenceTimeType = new TimeType(TimeTypes.WORKING_TYPE, "Absence", "", AppConstants.WORKING_TYPE_COLOR, ABSENCE, countryId, Collections.EMPTY_LIST);
        TimeType breakTimeType = new TimeType(TimeTypes.WORKING_TYPE, "Paid Break", "", AppConstants.WORKING_TYPE_COLOR, PAID_BREAK, countryId, Collections.EMPTY_LIST);
        workingTimeTypes.add(presenceTimeType);
        workingTimeTypes.add(absenceTimeType);
        workingTimeTypes.add(breakTimeType);

        List<TimeType> nonWorkingTimeTypes = new ArrayList<>();
        TimeType volunteerTimeType = new TimeType(TimeTypes.NON_WORKING_TYPE, "Volunteer Time", "", AppConstants.NON_WORKING_TYPE_COLOR, VOLUNTEER, countryId, Collections.EMPTY_LIST);
        TimeType timeBankOffTimeType = new TimeType(TimeTypes.NON_WORKING_TYPE, "Timebank Off Time", "", AppConstants.NON_WORKING_TYPE_COLOR, TIME_BANK, countryId, Collections.EMPTY_LIST);
        TimeType unPaidBreakTimeType = new TimeType(TimeTypes.NON_WORKING_TYPE, "Unpaid Break", "", AppConstants.NON_WORKING_TYPE_COLOR, UNPAID_BREAK, countryId, Collections.EMPTY_LIST);
        TimeType timeSplitInShiftTimeType = new TimeType(TimeTypes.NON_WORKING_TYPE, "Time between Split Shifts", "", AppConstants.NON_WORKING_TYPE_COLOR, SHIFT_SPLIT_TIME, countryId, Collections.EMPTY_LIST);
        TimeType dutyFreeTimeType = new TimeType(TimeTypes.NON_WORKING_TYPE, "Duty-free, Self-Paid", "", AppConstants.NON_WORKING_TYPE_COLOR, SELF_PAID, countryId, Collections.EMPTY_LIST);
        TimeType sicknessTimeType = new TimeType(TimeTypes.NON_WORKING_TYPE, "Planned Sickness on Freedays", "", AppConstants.NON_WORKING_TYPE_COLOR, PLANNED_SICK_ON_FREE_DAYS, countryId, Collections.EMPTY_LIST);
        TimeType unavailableTimeType = new TimeType(TimeTypes.NON_WORKING_TYPE, "Unavailable Time", "", AppConstants.NON_WORKING_TYPE_COLOR, UNAVAILABLE_TIME, countryId, Collections.EMPTY_LIST);
        TimeType restingTimeType = new TimeType(TimeTypes.NON_WORKING_TYPE, "Resting Time", "", AppConstants.NON_WORKING_TYPE_COLOR, RESTING_TIME, countryId, Collections.EMPTY_LIST);
        TimeType vetoTimeType = new TimeType(TimeTypes.NON_WORKING_TYPE, "Veto", "", AppConstants.NON_WORKING_TYPE_COLOR, VETO, countryId, Collections.EMPTY_LIST);
        TimeType stopBrickTimeType = new TimeType(TimeTypes.NON_WORKING_TYPE, "Stopbrick", "", AppConstants.NON_WORKING_TYPE_COLOR, STOP_BRICK, countryId, Collections.EMPTY_LIST);
        TimeType availableTimeType = new TimeType(TimeTypes.NON_WORKING_TYPE, "Available Time", "", AppConstants.NON_WORKING_TYPE_COLOR, AVAILABLE_TIME, countryId, Collections.EMPTY_LIST);

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

        allTimeTypes.addAll(workingTimeTypes);
        allTimeTypes.addAll(nonWorkingTimeTypes);

        save(allTimeTypes);

        return true;
    }
}
