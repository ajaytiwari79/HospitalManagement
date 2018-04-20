package com.kairos.activity.service.activity;


import com.kairos.activity.custom_exception.DuplicateDataException;
import com.kairos.activity.custom_exception.TimeTypeLinkedException;
import com.kairos.activity.enums.TimeTypes;
import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.persistence.model.activity.TimeType;
import com.kairos.activity.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.activity.persistence.repository.activity.ActivityMongoRepositoryImpl;
import com.kairos.activity.persistence.repository.activity.TimeTypeMongoRepository;
import com.kairos.activity.response.dto.activity.TimeTypeDTO;
import com.kairos.activity.service.MongoBaseService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TimeTypeService extends MongoBaseService {

    @Inject
    private TimeTypeMongoRepository timeTypeMongoRepository;
    @Inject
    private ActivityMongoRepositoryImpl activityMongoRepository;


    public List<TimeTypeDTO> createTimeType(List<TimeTypeDTO> timeTypeDTOs, Long countryId) {
        timeTypeDTOs.forEach(timeTypeDTO -> {
            TimeType timeType;
            if (timeTypeDTO.getTimeTypes() != null) {
                timeType = timeTypeMongoRepository.exists(timeTypeDTO.getLabel(), countryId);
                if (timeType == null) {
                    timeType = new TimeType(TimeTypes.getByValue(timeTypeDTO.getTimeTypes()), timeTypeDTO.getLabel(), timeTypeDTO.getDescription());
                    timeType.setCountryId(countryId);
                    if (timeTypeDTO.getUpperLevelTimeTypeId() != null) {
                        timeType.setUpperLevelTimeTypeId(timeTypeDTO.getUpperLevelTimeTypeId());
                    }
                    timeType = save(timeType);
                    if(timeTypeDTO.getUpperLevelTimeTypeId() != null){
                        TimeType parentTimeType = timeTypeMongoRepository.findOne(timeTypeDTO.getUpperLevelTimeTypeId());
                        parentTimeType.getChildTimeTypeIds().add(timeType.getId());
                        save(parentTimeType);
                    }
                    timeTypeDTO.setId(timeType.getId());
                } else {
                    throw new DuplicateDataException("Name already Exists");
                }
            }
        });
        return timeTypeDTOs;
    }

    public List<TimeTypeDTO> updateTimeType(List<TimeTypeDTO> timeTypeDTOS, Long countryId) {
        timeTypeDTOS.forEach(timeTypeDTO -> {
            TimeType timeType = timeTypeMongoRepository.exists(timeTypeDTO.getLabel(), countryId);
            if (timeType == null) {
                timeType = timeTypeMongoRepository.findOne(timeTypeDTO.getId());
                if (timeType != null) {
                    timeType.setLabel(timeTypeDTO.getLabel());
                    timeType.setDescription(timeTypeDTO.getDescription());
                    save(timeType);
                }
            } else {
                throw new DuplicateDataException("Name already Exists");
            }
        });
        return timeTypeDTOS;
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
                TimeTypeDTO timeTypeDTO = new TimeTypeDTO(timeType.getId(), timeType.getTimeTypes().toValue(), timeType.getLabel(), timeType.getDescription());
                if (timeTypeId != null && timeType.getId().equals(timeTypeId)) {
                    timeTypeDTO.setSelected(true);
                }
                timeTypeDTO.setTimeTypes(timeType.getTimeTypes().toValue());
                timeTypeDTO.setChildren(getLowerLevelTimeTypeDTOs(timeTypeId, timeType.getId(), timeTypes));
                parentOfWorkingTimeType.add(timeTypeDTO);
            } else {
                TimeTypeDTO timeTypeDTO = new TimeTypeDTO(timeType.getId(), timeType.getTimeTypes().toValue(), timeType.getLabel(), timeType.getDescription());
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

    public List<TimeTypeDTO> getLowerLevelTimeTypeDTOs(BigInteger timeTypeId, BigInteger upperlevelTimeTypeId, List<TimeType> timeTypes) {
        List<TimeTypeDTO> lowerLevelTimeTypeDTOS = new ArrayList<>();
        timeTypes.forEach(timeType -> {
            if (timeType.getUpperLevelTimeTypeId().equals(upperlevelTimeTypeId)) {
                TimeTypeDTO levelTwoTimeTypeDTO = new TimeTypeDTO(timeType.getId(), timeType.getTimeTypes().toValue(), timeType.getLabel(), timeType.getDescription());
                if (timeTypeId != null && timeType.getId().equals(timeTypeId)) {
                    levelTwoTimeTypeDTO.setSelected(true);
                }
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
            timeType.setDeleted(true);
            save(timeType);
        } else throw new TimeTypeLinkedException("TimeType is Linked");
        return false;
    }


}
