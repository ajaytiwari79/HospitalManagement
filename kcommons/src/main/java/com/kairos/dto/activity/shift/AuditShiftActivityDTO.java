package com.kairos.dto.activity.shift;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.commons.utils.ObjectUtils.isNullOrElse;

@Getter
@Setter
public class AuditShiftActivityDTO implements Comparable<AuditShiftActivityDTO>{
    private Date startDate;
    private Date endDate;
    private BigInteger activityId;
    private int durationMinutes;
    private List<AuditShiftActivityDTO> childActivities;

    public boolean isChanged(AuditShiftActivityDTO auditShiftActivityDTO){
        childActivities = isNullOrElse(childActivities,new ArrayList<>());
        auditShiftActivityDTO.setChildActivities(isNullOrElse(auditShiftActivityDTO.getChildActivities(),new ArrayList<>()));
        boolean isChanged = isNull(startDate) || isNull(endDate) || isNull(activityId);
        isChanged = isChanged || !(startDate.equals(auditShiftActivityDTO.startDate) || endDate.equals(auditShiftActivityDTO.endDate) || activityId.equals(auditShiftActivityDTO.activityId));
        isChanged = isChanged || childActivities.size()!=auditShiftActivityDTO.childActivities.size() || this.durationMinutes != auditShiftActivityDTO.durationMinutes;
        for (int i = 0; i < childActivities.size(); i++) {
            if (!isChanged) {
                AuditShiftActivityDTO activityDTO = childActivities.get(i);
                AuditShiftActivityDTO shiftActivityDTO = auditShiftActivityDTO.childActivities.get(i);
                isChanged = activityDTO.isChanged(shiftActivityDTO);
            }
        }
        return isChanged;
    }

    @Override
    public int compareTo(AuditShiftActivityDTO auditShiftActivityDTO) {
        return startDate.compareTo(auditShiftActivityDTO.startDate);
    }
}
