package com.kairos.dto.activity.wta.templates;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by pavan on 24/4/18.
 */
@Getter
@Setter
public class BreakTemplateValue {
    private int shiftDuration;
    private int breaksAllowed;
    private int breakDuration;
    private int earliestDurationMinutes;
    private int latestDurationMinutes;
    private List<Long> activities=new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BreakTemplateValue)) return false;
        BreakTemplateValue that = (BreakTemplateValue) o;
        return shiftDuration == that.shiftDuration &&
                breaksAllowed == that.breaksAllowed &&
                breakDuration == that.breakDuration &&
                earliestDurationMinutes == that.earliestDurationMinutes &&
                latestDurationMinutes == that.latestDurationMinutes &&
                Objects.equals(activities, that.activities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shiftDuration, breaksAllowed, breakDuration, earliestDurationMinutes, latestDurationMinutes, activities);
    }
}
