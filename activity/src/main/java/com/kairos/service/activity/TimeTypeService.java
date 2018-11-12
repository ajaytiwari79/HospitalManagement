package com.kairos.service.activity;


import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.TimeTypes;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.repository.activity.ActivityMongoRepositoryImpl;
import com.kairos.persistence.repository.time_type.TimeTypeMongoRepository;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TimeTypeService extends MongoBaseService {

    @Inject
    private TimeTypeMongoRepository timeTypeMongoRepository;
    @Inject
    private ActivityMongoRepositoryImpl activityMongoRepository;
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
        BigInteger upperLevelTimeTypeId=timeTypeDTOs.get(0).getUpperLevelTimeTypeId();
        TimeType upperTimeType=timeTypeMongoRepository.findOneById(upperLevelTimeTypeId);
        timeTypeDTOs.forEach(timeTypeDTO -> {
            TimeType timeType;
            if (timeTypeDTO.getTimeTypes() != null && timeTypeDTO.getUpperLevelTimeTypeId() != null) {
                timeType = new TimeType(TimeTypes.getByValue(timeTypeDTO.getTimeTypes()), timeTypeDTO.getLabel(), timeTypeDTO.getDescription(), timeTypeDTO.getBackgroundColor(),upperTimeType.getSecondLevelType(),countryId);
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

    public List<TimeTypeDTO> updateTimeType(List<TimeTypeDTO> timeTypeDTOS, Long countryId) {
        List<TimeType> timeTypes = new ArrayList<>();
        List<BigInteger> timeTypeIds = timeTypeDTOS.stream().map(timeTypeId -> timeTypeId.getId()).collect(Collectors.toList());
        List<String> timeTypeLabels = timeTypeDTOS.stream().map(timeTypeId -> timeTypeId.getLabel()).collect(Collectors.toList());
        Boolean timeTypesExists = timeTypeMongoRepository.findByIdNotEqualAndLabelAndCountryId(timeTypeIds, timeTypeLabels, countryId);
        if (timeTypesExists) {
            exceptionService.duplicateDataException("message.timetype.name.alreadyexist");
        }
        List<TimeType> timeTypesResult = timeTypeMongoRepository.findAllByTimeTypeIds(timeTypeIds);
        Map<BigInteger, TimeType> timeTypeMap = timeTypesResult.stream().collect(Collectors.toMap(timetype -> timetype.getId(), timetype -> timetype));
        List<TimeType> childTimeTypes = timeTypeMongoRepository.findAllChildTimeTypeByParentId(timeTypeIds);
        Map<BigInteger, List<TimeType>> childTimeTypesMap = childTimeTypes.stream().collect(Collectors.groupingBy(t -> t.getUpperLevelTimeTypeId(), Collectors.toList()));
        List<BigInteger> childTimeTypeIds = childTimeTypes.stream().map(timetype -> timetype.getId()).collect(Collectors.toList());
        List<TimeType> leafTimeTypes = timeTypeMongoRepository.findAllChildTimeTypeByParentId(childTimeTypeIds);
        Map<BigInteger, List<TimeType>> leafTimeTypesMap = leafTimeTypes.stream().collect(Collectors.groupingBy(timetype -> timetype.getUpperLevelTimeTypeId(), Collectors.toList()));
        timeTypeDTOS.forEach(timeTypeDTO -> {
            TimeType timeType = timeTypeMap.get(timeTypeDTO.getId());
            if (Optional.ofNullable(timeType).isPresent()) {
                timeType.setLabel(timeTypeDTO.getLabel());
                timeType.setDescription(timeTypeDTO.getDescription());
                timeType.setBackgroundColor(timeTypeDTO.getBackgroundColor());
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
            }

        });
        save(timeTypes);
        return timeTypeDTOS;
    }

    //TODO By Yasir:- CO-ordinate with front-end to send and receive single time type in api and use below method instead of above.
    public TimeTypeDTO updateTimeType(TimeTypeDTO timeTypeDTO, Long countryId) {

        Boolean timeTypesExists = timeTypeMongoRepository.timeTypeAlreadyExistsByLabelAndCountryId(timeTypeDTO.getId(), timeTypeDTO.getLabel(), countryId);
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
        TimeTypeDTO workingTimeTypeDTO = new TimeTypeDTO(TimeTypes.WORKING_TYPE.toValue());
        TimeTypeDTO nonWorkingTimeTypeDTO = new TimeTypeDTO(TimeTypes.NON_WORKING_TYPE.toValue());
        if (topLevelTimeTypes.isEmpty()) {
            timeTypeDTOS.add(workingTimeTypeDTO);
            timeTypeDTOS.add(nonWorkingTimeTypeDTO);
            return timeTypeDTOS;
        }
        List<TimeType> timeTypes = timeTypeMongoRepository.findAllLowerLevelTimeType(countryId);
        List<TimeTypeDTO> parentOfWorkingTimeType = new ArrayList<>();
        List<TimeTypeDTO> parentOfNonWorkingTimeType = new ArrayList<>();
        for (TimeType timeType : topLevelTimeTypes) {
            if (timeType.getTimeTypes().equals(TimeTypes.WORKING_TYPE)) {
                TimeTypeDTO timeTypeDTO = new TimeTypeDTO(timeType.getId(), timeType.getTimeTypes().toValue(), timeType.getLabel(), timeType.getDescription(), timeType.getBackgroundColor());
                timeTypeDTO.setSecondLevelType(timeType.getSecondLevelType());
                if (timeTypeId != null && timeType.getId().equals(timeTypeId)) {
                    timeTypeDTO.setSelected(true);
                }
                timeTypeDTO.setTimeTypes(timeType.getTimeTypes().toValue());
                timeTypeDTO.setChildren(getLowerLevelTimeTypeDTOs(timeTypeId, timeType.getId(), timeTypes));
                parentOfWorkingTimeType.add(timeTypeDTO);
            } else {
                TimeTypeDTO timeTypeDTO = new TimeTypeDTO(timeType.getId(), timeType.getTimeTypes().toValue(), timeType.getLabel(), timeType.getDescription(), timeType.getBackgroundColor());
                timeTypeDTO.setSecondLevelType(timeType.getSecondLevelType());
                if (timeTypeId != null && timeType.getId().equals(timeTypeId)) {
                    timeTypeDTO.setSelected(true);
                }
                timeTypeDTO.setTimeTypes(timeType.getTimeTypes().toValue());
                timeTypeDTO.setChildren(getLowerLevelTimeTypeDTOs(timeTypeId, timeType.getId(), timeTypes));
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
                presenceAbsenceTimeTypeMap.put("Presence",presenceTimeTypes);
            } else if (t.getLabel().equals("Absence")) {
                List<TimeType> absenceTimeTypes = getChildOfTimeType(t, timeTypeMap);
                absenceTimeTypes.add(t);
                presenceAbsenceTimeTypeMap.put("Absence", absenceTimeTypes);
            }

        });
        return presenceAbsenceTimeTypeMap;
    }


    public List<TimeType> getChildOfTimeType(TimeType timeType, Map<BigInteger, List<TimeType>> timeTypeMap) {
        List<TimeType> timeTypes1 = new ArrayList<>();
        List<TimeType> secondLevelTimeTypes = null;
        secondLevelTimeTypes = timeTypeMap.get(timeType.getId());
        timeTypes1.addAll(secondLevelTimeTypes);
        if (secondLevelTimeTypes != null) {
            secondLevelTimeTypes.forEach(t -> {
                List<TimeType> leaftimeTypes = timeTypeMap.get(t.getId());
                if (leaftimeTypes != null) {
                    timeTypes1.addAll(leaftimeTypes);
                }
            });

        }

        return timeTypes1;
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

    public List<BigInteger> getTimeTypesByTimeTypesAndByCountryId(Long countryId, TimeTypes timeType){
        List<TimeType> timeTypeList = timeTypeMongoRepository.findByTimeTypeEnumAndCountryId(countryId, timeType);
        if(timeTypeList.isEmpty()) return new ArrayList<>();
        return timeTypeList.parallelStream().map(timeType1 -> timeType1.getId()).collect(Collectors.toList());
    }

    public List<TimeTypeDTO> getLowerLevelTimeTypeDTOs(BigInteger timeTypeId, BigInteger upperlevelTimeTypeId, List<TimeType> timeTypes) {
        List<TimeTypeDTO> lowerLevelTimeTypeDTOS = new ArrayList<>();
        timeTypes.forEach(timeType -> {
            if (timeType.getUpperLevelTimeTypeId().equals(upperlevelTimeTypeId)) {
                TimeTypeDTO levelTwoTimeTypeDTO = new TimeTypeDTO(timeType.getId(), timeType.getTimeTypes().toValue(), timeType.getLabel(), timeType.getDescription(), timeType.getBackgroundColor());
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


    public List<BigInteger> getAllParentTimeTypeByTimeTypeId(List<BigInteger> timeTypeIds, Long countryId) {
        List<TimeType> timeTypes = timeTypeMongoRepository.findAllByCountryId(countryId);
        Set<BigInteger> timeTypeIdsWithChildrens = new HashSet<>();
      /*  timeTypeIds.forEach(tt->{
            timeTypes.forEach(timeType -> {
                if(timeTypeIds.contains(timeType.getId())){
                    timeTypeIdsWithChildrens.addAll(timeType.getChildTimeTypeIds());
                    if(timeType.getChildTimeTypeIds().contains(timeType.getId())){
                        timeTypeIdsWithChildrens.addAll(timeType.getChildTimeTypeIds());
                    }
                }
            });

        });*/
        timeTypeIdsWithChildrens.addAll(timeTypeIds);
        return new ArrayList<>(timeTypeIdsWithChildrens);
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


    public Boolean createDefaultTimeType(Long countryId) {
        List<TimeType> allTimeTypes=new ArrayList<>();
        List<TimeType> workingTimeTypes=new ArrayList<>();
        TimeType presenceTimeType=new TimeType(TimeTypes.WORKING_TYPE, "Presence", "", "",TimeTypeEnum.PRESENCE,countryId);
        TimeType absenceTimeType=new TimeType(TimeTypes.WORKING_TYPE, "Absence", "", "",TimeTypeEnum.ABSENCE,countryId);
        TimeType breakTimeType=new TimeType(TimeTypes.WORKING_TYPE, "Paid Break", "", "",TimeTypeEnum.BREAK,countryId);
        workingTimeTypes.add(presenceTimeType);
        workingTimeTypes.add(absenceTimeType);
        workingTimeTypes.add(breakTimeType);

        List<TimeType> nonWorkingTimeTypes=new ArrayList<>();
        TimeType volunteerTimeType=new TimeType(TimeTypes.NON_WORKING_TYPE, "volunteer time", "", "",null,countryId);
        TimeType timeBankOffTimeType=new TimeType(TimeTypes.NON_WORKING_TYPE, "timebank off time", "", "",null,countryId);
        TimeType unPaidBreakTimeType=new TimeType(TimeTypes.NON_WORKING_TYPE, "Unpaid break", "", "",TimeTypeEnum.BREAK,countryId);
        TimeType timeSplitInShiftTimeType=new TimeType(TimeTypes.NON_WORKING_TYPE, "time between split shifts", "", "",null,countryId);
        TimeType dutyFreeTimeType=new TimeType(TimeTypes.NON_WORKING_TYPE, "duty-free, selfpaid", "", "",null,countryId);
        TimeType sicknessTimeType=new TimeType(TimeTypes.NON_WORKING_TYPE, "planned sickness on freedays", "", "",null,countryId);
        TimeType unavailableTimeType=new TimeType(TimeTypes.NON_WORKING_TYPE, "unavailable time", "", "",null,countryId);
        TimeType restingTimeType=new TimeType(TimeTypes.NON_WORKING_TYPE, "resting time", "", "",null,countryId);
        TimeType vetoTimeType=new TimeType(TimeTypes.NON_WORKING_TYPE, "Veto", "", "",null,countryId);
        TimeType stopBrickTimeType=new TimeType(TimeTypes.NON_WORKING_TYPE, "Stopbrick", "", "",null,countryId);
        TimeType availableTimeType=new TimeType(TimeTypes.NON_WORKING_TYPE, "available time", "", "",null,countryId);

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
