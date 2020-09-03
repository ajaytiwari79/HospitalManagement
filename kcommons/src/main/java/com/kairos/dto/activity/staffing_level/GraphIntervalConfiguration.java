package com.kairos.dto.activity.staffing_level;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GraphIntervalConfiguration {
    private int startPercentage;
    private int endPercentage;
    private String colorCode;
}
