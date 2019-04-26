package com.kairos.service.priority_group;

import com.kairos.dto.activity.open_shift.FibonacciCounter;
import com.kairos.dto.user.staff.employment.StaffEmploymentQueryResult;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FibonacciCounterApply {



    public FibonacciCounterApply() {

    }


    public List<FibonacciCounter> calculateFibonacciCounter(List<StaffEmploymentQueryResult> staffsEmployments, Map<Long,Integer> assignedOpenShiftMap) {

        List<FibonacciCounter> fibonacciCounters = new ArrayList<FibonacciCounter>();

        Iterator<StaffEmploymentQueryResult> staffEmploymentIterator = staffsEmployments.iterator();
        while(staffEmploymentIterator.hasNext()) {
            StaffEmploymentQueryResult staffEmploymentQueryResult = staffEmploymentIterator.next();
            FibonacciCounter fibonacciCounter = new FibonacciCounter(staffEmploymentQueryResult.getStaffId(), staffEmploymentQueryResult.getAccumulatedTimeBank(),
                    Optional.ofNullable(assignedOpenShiftMap.get(staffEmploymentQueryResult.getEmploymentId())).isPresent()?
                            assignedOpenShiftMap.get(staffEmploymentQueryResult.getEmploymentId()):0);
            fibonacciCounters.add(fibonacciCounter);
        }
        return fibonacciCounters;
    }

    public List<FibonacciCounter> findBestCandidates(ImpactWeight impactWeight, List<StaffEmploymentQueryResult> staffsEmployments, Map<Long,Integer> assignedOpenShiftMap) {

        List<FibonacciCounter> fibonacciCounters = calculateFibonacciCounter(staffsEmployments,assignedOpenShiftMap);
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
