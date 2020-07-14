package com.kairos.service.activity;

import com.kairos.persistence.model.activity.StaffActivityDetails;
import com.kairos.persistence.repository.activity.StaffActivityDetailsMongoRepository;
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
public class StaffActivityDetailsService {
    @Inject
    private StaffActivityDetailsMongoRepository staffActivityDetailsMongoRepository;
    @Inject
    private ActivityService activityService;

    @Async
    public void updateStaffActivityDetails(Long staffId, List<BigInteger> newShiftActivityIds, List<BigInteger> oldShiftActivityIds){
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
        List<StaffActivityDetails> staffActivityDetails = staffActivityDetailsMongoRepository.findByDeletedFalseAndStaffIdAndActivityIdIn(staffId, activityIds);
        Map<BigInteger, StaffActivityDetails> activityUseCountMap = staffActivityDetails.stream().collect(Collectors.toMap(StaffActivityDetails::getActivityId, v->v));
        List<StaffActivityDetails> updatedStaffActivityDetails = new ArrayList<>();
        for (BigInteger activityId : activityIds) {
            StaffActivityDetails staffActivityDetail;
            if(activityUseCountMap.containsKey(activityId)){
                staffActivityDetail = activityUseCountMap.get(activityId);
                staffActivityDetail.setUseActivityCount(staffActivityDetail.getUseActivityCount() + 1);
            }else{
                staffActivityDetail = new StaffActivityDetails(staffId, activityId, 1);
            }
            updatedStaffActivityDetails.add(staffActivityDetail);
        }
        if(isCollectionNotEmpty(updatedStaffActivityDetails)) {
            staffActivityDetailsMongoRepository.saveEntities(updatedStaffActivityDetails);
        }
    }

    @Async
    public void decreaseActivityCount(Long staffId, List<BigInteger> activityIds){
        List<StaffActivityDetails> staffActivityDetails = staffActivityDetailsMongoRepository.findByDeletedFalseAndStaffIdAndActivityIdIn(staffId, activityIds);
        for (StaffActivityDetails staffActivityDetail : staffActivityDetails) {
            if(staffActivityDetail.getUseActivityCount() > 0){
                staffActivityDetail.setUseActivityCount(staffActivityDetail.getUseActivityCount() - 1);
            }
        }
        if(isCollectionNotEmpty(staffActivityDetails)) {
            staffActivityDetailsMongoRepository.saveEntities(staffActivityDetails);
        }
    }

    public Map<BigInteger, StaffActivityDetails> getMapOfActivityAndStaffActivityUseCount(Long staffId) {
        List<StaffActivityDetails> staffActivityDetails = staffActivityDetailsMongoRepository.findByDeletedFalseAndStaffId(staffId);
        return staffActivityDetails.stream().collect(Collectors.toMap(StaffActivityDetails::getActivityId, v->v));
    }
}
