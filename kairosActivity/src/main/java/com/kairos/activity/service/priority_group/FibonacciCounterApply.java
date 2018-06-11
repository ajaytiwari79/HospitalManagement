package com.kairos.activity.service.priority_group;

import com.kairos.activity.persistence.model.open_shift.OpenShift;
import com.kairos.activity.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.activity.response.dto.time_bank.UnitPositionWithCtaDetailsDTO;
import com.kairos.activity.util.DateUtils;
import com.kairos.activity.util.time_bank.TimeBankCalculationService;
import com.kairos.response.dto.web.StaffUnitPositionQueryResult;
import com.kairos.response.dto.web.open_shift.FibonacciCounter;
import org.joda.time.Interval;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FibonacciCounterApply {



    public FibonacciCounterApply() {

    }


    public List<FibonacciCounter> calculateFibonacciCounter(List<StaffUnitPositionQueryResult> staffsUnitPositions,Map<Long,Integer> assignedOpenShiftMap) {

        List<FibonacciCounter> fibonacciCounters = new ArrayList<FibonacciCounter>();

        Iterator<StaffUnitPositionQueryResult> staffUnitPositionIterator = staffsUnitPositions.iterator();
        while(staffUnitPositionIterator.hasNext()) {
            StaffUnitPositionQueryResult staffUnitPositionQueryResult = staffUnitPositionIterator.next();
            FibonacciCounter fibonacciCounter = new FibonacciCounter(staffUnitPositionQueryResult.getStaffId(),staffUnitPositionQueryResult.getAccumulatedTimeBank(),
                    Optional.ofNullable(assignedOpenShiftMap.get(staffUnitPositionQueryResult.getUnitPositionId())).isPresent()?
                            assignedOpenShiftMap.get(staffUnitPositionQueryResult.getUnitPositionId()):0);
            fibonacciCounters.add(fibonacciCounter);
        }
        return fibonacciCounters;
    }

    public List<FibonacciCounter> findBestCandidates(ImpactWeight impactWeight,List<StaffUnitPositionQueryResult> staffsUnitPositions,Map<Long,Integer> assignedOpenShiftMap) {

        List<FibonacciCounter> fibonacciCounters = calculateFibonacciCounter(staffsUnitPositions,assignedOpenShiftMap);
        fibonacciCounters.sort((FibonacciCounter f1,FibonacciCounter f2)->f1.getTimeBank()-f2.getTimeBank());

                int i = 0;
        for(FibonacciCounter fibonacciCounter:fibonacciCounters) {
            if(i==0||i==1){
                fibonacciCounter.setFibonacciTimeBank(1*impactWeight.getTimBankImpact());
            }
            else {
                fibonacciCounter.setFibonacciTimeBank((fibonacciCounters.get(i-2).getFibonacciTimeBank()+fibonacciCounters.get(i-1).getFibonacciTimeBank()));
            }
            i++;
        }

        i = 0;
        fibonacciCounters.sort((FibonacciCounter f1,FibonacciCounter f2)->f1.getAssignedOpenShifts()-f2.getAssignedOpenShifts());
        for(FibonacciCounter fibonacciCounter:fibonacciCounters) {
            if(i==0||i==1){
                fibonacciCounter.setFibonacciAssignedOpenShifts(1*impactWeight.getAssignedOpenShiftImpact());
            }
            else {
                fibonacciCounter.setFibonacciAssignedOpenShifts((fibonacciCounters.get(i-2).getFibonacciAssignedOpenShifts()+
                        fibonacciCounters.get(i-1).getFibonacciAssignedOpenShifts()));
            }
            fibonacciCounter.setCountersSum(fibonacciCounter.getFibonacciAssignedOpenShifts()+
                    fibonacciCounter.getFibonacciTimeBank());
            i++;
        }

        fibonacciCounters.sort((FibonacciCounter f1,FibonacciCounter f2)->f1.getCountersSum()-f2.getCountersSum());


        return fibonacciCounters;
    }
}
