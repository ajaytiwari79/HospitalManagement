package com.kairos.persistence.model.user.expertise;

import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;

import java.time.LocalDate;

@Getter
@Setter
@NodeEntity
public class ProtectedDaysOffSetting extends UserBaseEntity implements Comparable<ProtectedDaysOffSetting>{
    private Long holidayId;
    private LocalDate publicHolidayDate;
    private boolean protectedDaysOff;
    private Long dayTypeId;

    @Override
    public int compareTo(ProtectedDaysOffSetting protectedDaysOffSetting) {
        return 0;
    }
}
