package com.kairos.shiftplanning.domain.activity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.joda.time.Interval;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShiftActivity {
    private DateTime startTime;
    private Activity activity;
    private DateTime endTime;

    public Interval getInterval() {
        return new Interval(this.startTime,this.endTime);
    }
}
