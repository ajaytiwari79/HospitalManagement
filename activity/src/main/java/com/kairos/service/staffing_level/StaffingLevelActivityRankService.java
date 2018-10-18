package com.kairos.service.staffing_level;
/*
 *Created By Pavan on 10/10/18
 *
 */

import com.kairos.persistence.model.staffing_level.StaffingLevelActivityRank;
import com.kairos.persistence.repository.staffing_level.StaffingLevelActivityRankRepository;
import com.kairos.service.MongoBaseService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
@Service
public class StaffingLevelActivityRankService extends MongoBaseService {
    @Inject
    private StaffingLevelActivityRankRepository staffingLevelActivityRankRepository;

     boolean updateStaffingLevelActivityRank(LocalDate staffingLevelDate, BigInteger staffingLevelId, Map<BigInteger, Integer> activitiesRankMap) {
        List<StaffingLevelActivityRank> staffingLevelActivityRank = staffingLevelActivityRankRepository.findAllByStaffingLevelIdAndStaffingLevelDateAndDeletedFalse(staffingLevelId,staffingLevelDate);
        Map<BigInteger, StaffingLevelActivityRank> staffingLevelActivityRankingMap = staffingLevelActivityRank.stream().collect(Collectors.toMap(StaffingLevelActivityRank::getActivityId, Function.identity(),(current,previous)->current));
        List<StaffingLevelActivityRank> staffingLevelActivityRanks = constructObjects(activitiesRankMap, staffingLevelActivityRankingMap,staffingLevelId,staffingLevelDate);
        if(!staffingLevelActivityRanks.isEmpty()){
            save(staffingLevelActivityRanks);
        }
        return true;
    }

    private List<StaffingLevelActivityRank> constructObjects(Map<BigInteger, Integer> activitiesRankMap, Map<BigInteger, StaffingLevelActivityRank> staffingLevelActivityRankingMap, BigInteger staffingLevelId, LocalDate staffingLevelDate) {
        List<StaffingLevelActivityRank> staffingLevelActivityRanks = new ArrayList<>();
        activitiesRankMap.forEach((k, v) -> {
            if (staffingLevelActivityRankingMap.get(k) != null) {
                staffingLevelActivityRanks.add(new StaffingLevelActivityRank(staffingLevelActivityRankingMap.get(k).getId(), k, staffingLevelDate, staffingLevelId, v));
            }
            else{
                staffingLevelActivityRanks.add(new StaffingLevelActivityRank(k, staffingLevelDate, staffingLevelId, v));
            }
        });
        return staffingLevelActivityRanks;
    }
}
