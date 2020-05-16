package com.kairos.dto.activity.open_shift.priority_group;

import com.kairos.enums.DayCheckPeriod;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class ScheduledProcess {
    private DayCheckPeriod dayCheckPeriod; // to show the content selected e.g. Monday,Tuesday,Wednesday,Thursday,Friday. Every 60 minute
    private Integer startMinute;//
    private Integer selectedRepeatInterval;//
    private Set<DayOfWeek> selectedDays;//
    private Integer runOnce;//
    private List<String> selectedTimes;//


    public ScheduledProcess(DayCheckPeriod dayCheckPeriod, Integer startMinute, Integer selectedRepeatInterval,
                            Set<DayOfWeek> selectedDays, Integer runOnce, List<String> selectedTimes) {
        this.dayCheckPeriod = dayCheckPeriod;
        this.startMinute = startMinute;
        this.selectedRepeatInterval = selectedRepeatInterval;
        this.selectedDays = selectedDays;
        this.runOnce = runOnce;
        this.selectedTimes = selectedTimes;
    }

}
