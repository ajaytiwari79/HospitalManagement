package com.kairos.dto.activity.shift;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.enums.audit_logging.LoggingType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;
import static java.lang.Math.abs;

@Getter
@Setter
public class AuditShiftDTO {
    private Date startDate;
    private Date old_startDate;
    private Date endDate;
    private Date old_endDate;
    private Long staffId;
    private List<AuditShiftActivityDTO> activities;
    private List<AuditShiftActivityDTO> old_activities;
    private LoggingType loggingType;

    public boolean isChanged(){
        activities = isNullOrElse(activities,new ArrayList<>());
        old_activities = isNullOrElse(old_activities,new ArrayList<>());
        boolean isChanged = LoggingType.DELETED.equals(this.loggingType) || activities.size()!=old_activities.size();
        for (int i = 0; i < activities.size(); i++) {
            if (!isChanged) {
                AuditShiftActivityDTO activityDTO = activities.get(i);
                AuditShiftActivityDTO shiftActivityDTO = old_activities.get(i);
                isChanged = activityDTO.isChanged(shiftActivityDTO);
            }
        }
        return isChanged;
    }

    public int getChangedHours() {
        Collections.sort(activities);
        int changeMinutes = 0;
        if(LoggingType.DELETED.equals(loggingType)){
            this.startDate = activities.get(0).getStartDate();
            this.endDate = activities.get(activities.size()-1).getEndDate();
            changeMinutes = (int)new DateTimeInterval(startDate,endDate).getMinutes();
        }else {
            int totalMinutes = getMinutesOfActivity(activities);
            int oldTotalMinutes = getMinutesOfActivity(old_activities);
            if(totalMinutes==oldTotalMinutes){
                totalMinutes = activities.stream().collect(Collectors.summingInt(auditShiftActivityDTO -> auditShiftActivityDTO.getDurationMinutes()));
                oldTotalMinutes = old_activities.stream().collect(Collectors.summingInt(auditShiftActivityDTO -> auditShiftActivityDTO.getDurationMinutes()));
            }
            changeMinutes = abs(totalMinutes-oldTotalMinutes);
        }
        return changeMinutes;
    }

    private int getMinutesOfActivity(List<AuditShiftActivityDTO> activities) {
        int intervalMinutes = 0;
        if(isCollectionNotEmpty(activities)){
            Collections.sort(activities);
            this.startDate = activities.get(0).getStartDate();
            this.endDate = activities.get(activities.size() - 1).getEndDate();
            intervalMinutes = (int)new DateTimeInterval(startDate, endDate).getMinutes();
        }
        return 0;
    }
}
