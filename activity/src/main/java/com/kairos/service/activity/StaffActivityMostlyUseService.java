package com.kairos.service.activity;

import com.kairos.persistence.model.activity.StaffActivityMostlyUse;
import com.kairos.persistence.repository.activity.StaffActivityMostlyUseMongoRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;


@Service
public class StaffActivityMostlyUseService {
    @Inject
    private StaffActivityMostlyUseMongoRepository staffActivityMostlyUseMongoRepository;
    @Inject
    private ActivityService activityService;

    @Async
    public void updateStaffActivityCount(Long staffId, List<BigInteger> newShiftActivityIds, List<BigInteger> oldShiftActivityIds){
        if(isCollectionEmpty(oldShiftActivityIds)){
            increaseActivityCount(staffId, newShiftActivityIds);
        } else {
            List<BigInteger> increaseCountList = new ArrayList<>(newShiftActivityIds);
            List<BigInteger> decreaseCountList = new ArrayList<>(oldShiftActivityIds);
            increaseCountList.removeAll(oldShiftActivityIds);
            if(isCollectionNotEmpty(increaseCountList)){
                increaseActivityCount(staffId, increaseCountList);
            }
            decreaseCountList.removeAll(newShiftActivityIds);
            if(isCollectionNotEmpty(decreaseCountList)){
                decreaseActivityCount(staffId, decreaseCountList);
            }
        }
    }

    @Async
    public void increaseActivityCount(Long staffId, List<BigInteger> activityIds){
        List<StaffActivityMostlyUse> staffActivityMostlyUses = staffActivityMostlyUseMongoRepository.findByDeletedFalseAndStaffIdAndActivityIdIn(staffId, activityIds);
        Map<BigInteger, StaffActivityMostlyUse> activityUseCountMap = staffActivityMostlyUses.stream().collect(Collectors.toMap(StaffActivityMostlyUse::getActivityId, v->v));
        List<StaffActivityMostlyUse> updatedStaffActivityMostlyUses = new ArrayList<>();
        for (BigInteger activityId : activityIds) {
            StaffActivityMostlyUse staffActivityMostlyUse;
            if(activityUseCountMap.containsKey(activityId)){
                staffActivityMostlyUse = activityUseCountMap.get(activityId);
                staffActivityMostlyUse.setUseActivityCount(staffActivityMostlyUse.getUseActivityCount() + 1);
            }else{
                staffActivityMostlyUse = new StaffActivityMostlyUse(staffId, activityId, 1);
            }
            updatedStaffActivityMostlyUses.add(staffActivityMostlyUse);
        }
        if(isCollectionNotEmpty(updatedStaffActivityMostlyUses)) {
            staffActivityMostlyUseMongoRepository.saveEntities(updatedStaffActivityMostlyUses);
        }
    }

    @Async
    public void decreaseActivityCount(Long staffId, List<BigInteger> activityIds){
        List<StaffActivityMostlyUse> staffActivityMostlyUses = staffActivityMostlyUseMongoRepository.findByDeletedFalseAndStaffIdAndActivityIdIn(staffId, activityIds);
        for (StaffActivityMostlyUse staffActivityMostlyUse : staffActivityMostlyUses) {
            if(staffActivityMostlyUse.getUseActivityCount() > 0){
                staffActivityMostlyUse.setUseActivityCount(staffActivityMostlyUse.getUseActivityCount() - 1);
            }
        }
        if(isCollectionNotEmpty(staffActivityMostlyUses)) {
            staffActivityMostlyUseMongoRepository.saveEntities(staffActivityMostlyUses);
        }
    }

    public Map<BigInteger, StaffActivityMostlyUse> getMapOfActivityAndStaffActivityUseCount(Long staffId) {
        List<StaffActivityMostlyUse> staffActivityMostlyUses = staffActivityMostlyUseMongoRepository.findByDeletedFalseAndStaffId(staffId);
        return staffActivityMostlyUses.stream().collect(Collectors.toMap(StaffActivityMostlyUse::getActivityId, v->v));
    }
}
