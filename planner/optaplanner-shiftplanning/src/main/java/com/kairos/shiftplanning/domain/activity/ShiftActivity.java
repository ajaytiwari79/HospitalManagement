package com.kairos.shiftplanning.domain.activity;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

@Getter
@Setter
public class ShiftActivity {
    private DateTime startTime;
    private Activity activity;
    private DateTime endTime;
}
