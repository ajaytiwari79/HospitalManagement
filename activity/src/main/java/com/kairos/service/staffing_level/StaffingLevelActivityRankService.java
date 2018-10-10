package com.kairos.service.staffing_level;
/*
 *Created By Pavan on 10/10/18
 *
 */

import com.kairos.persistence.model.staffing_level.StaffingLevelActivityRanking;
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
        List<StaffingLevelActivityRanking> staffingLevelActivityRanking = staffingLevelActivityRankRepository.findAllByStaffingLevelIdAndStaffingLevelDateAndDeletedFalse();
        Map<BigInteger, StaffingLevelActivityRanking> staffingLevelActivityRankingMap = staffingLevelActivityRanking.stream().collect(Collectors.toMap(StaffingLevelActivityRanking::getId, Function.identity()));
        List<StaffingLevelActivityRanking> staffingLevelActivityRankings = constructObjects(activitiesRankMap, staffingLevelActivityRankingMap,staffingLevelId,staffingLevelDate);
        if(!staffingLevelActivityRankings.isEmpty()){
            save(staffingLevelActivityRankings);
        }
        return true;
    }

    private List<StaffingLevelActivityRanking> constructObjects(Map<BigInteger, Integer> activitiesRankMap, Map<BigInteger, StaffingLevelActivityRanking> staffingLevelActivityRankingMap, BigInteger staffingLevelId,LocalDate staffingLevelDate) {
        List<StaffingLevelActivityRanking> staffingLevelActivityRankings = new ArrayList<>();
        activitiesRankMap.forEach((k, v) -> {
            if (staffingLevelActivityRankingMap.get(k) != null) {
                staffingLevelActivityRankings.add(new StaffingLevelActivityRanking(staffingLevelActivityRankingMap.get(k).getId(), k, staffingLevelDate, staffingLevelId, v));
            }
            else{
                staffingLevelActivityRankings.add(new StaffingLevelActivityRanking(k, staffingLevelDate, staffingLevelId, v));
            }
        });
        return staffingLevelActivityRankings;
    }
}
