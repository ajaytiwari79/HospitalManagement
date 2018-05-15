package com.kairos.activity.util.priority_group;

import com.kairos.activity.enums.PriorityGroup.DayCheckPeriod;
import com.kairos.activity.enums.PriorityGroup.PriorityGroupName;
import com.kairos.activity.persistence.model.priority_group.*;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PriorityGroupUtil {

    public static List<PriorityGroup> getDefaultDataForPriorityGroup(long countryId){
        List<PriorityGroup> priorityGroups = new ArrayList<>();
        OpenShiftCancelProcess openShiftCancelProcess = new OpenShiftCancelProcess(true, false, true, true, false);
        RoundRules roundRules = new RoundRules(10, 10, 10, 10);
        StaffExcludeFilter staffExcludeFilter = new StaffExcludeFilter(false, 10, 10, 10, 10, 10, 10,
                10, 10, false, 10, 10, 10, false, false, false);
        StaffIncludeFilter staffIncludeFilter = new StaffIncludeFilter(false, false, false, false, false, new ArrayList<Long>(),5000,78.5f,2000);
        Set<DayOfWeek> dayOfWeeks=new LinkedHashSet<>();
        List<String> selectedTimes=new ArrayList<>();
        selectedTimes.add("0100-1200");
        dayOfWeeks.add(DayOfWeek.MONDAY);
        ScheduledProcess scheduledProcess=new ScheduledProcess(DayCheckPeriod.LAST,10,20,dayOfWeeks,1100,selectedTimes);
        PriorityGroup priorityGroup1 = new PriorityGroup(PriorityGroupName.PRIORITY_GROUP1,false, openShiftCancelProcess, roundRules, staffExcludeFilter, staffIncludeFilter, countryId, null,scheduledProcess);
        PriorityGroup priorityGroup2 = new PriorityGroup(PriorityGroupName.PRIORITY_GROUP2,false, openShiftCancelProcess, roundRules, staffExcludeFilter, staffIncludeFilter, countryId, null,scheduledProcess);
        PriorityGroup priorityGroup3 = new PriorityGroup(PriorityGroupName.PRIORITY_GROUP3,false, openShiftCancelProcess, roundRules, staffExcludeFilter, staffIncludeFilter, countryId, null,scheduledProcess);
        PriorityGroup priorityGroup4 = new PriorityGroup(PriorityGroupName.PRIORITY_GROUP4,false, openShiftCancelProcess, roundRules, staffExcludeFilter, staffIncludeFilter, countryId, null,scheduledProcess);
        priorityGroups.add(priorityGroup1);
        priorityGroups.add(priorityGroup2);
        priorityGroups.add(priorityGroup3);
        priorityGroups.add(priorityGroup4);
        return priorityGroups;

    }
}
