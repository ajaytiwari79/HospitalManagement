package com.kairos.dto.activity.staffing_level;

import com.kairos.commons.utils.DateTimeInterval;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class StaffingLevelPublishDTO {
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date startDate;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date endDate;
    private Set<LocalDate> weekDates;
    private LocalDate selectedDateForPresence;
    private LocalDate selectedEndDateForPresence;
    private LocalDate selectedDateForAbsence;
    private LocalDate selectedEndDateForAbsence;
    private Set<BigInteger> activityIds=new HashSet<>();
    private Set<Long> skillIds=new HashSet<>();
    private Date startTime;
    private Date endTime;

    public DateTimeInterval getInterval(){
        if(this.startTime==null || this.endTime==null){
            return null;
        }
        return new DateTimeInterval(this.startTime,this.endTime);
    }

}
