package com.kairos.dto.activity.staffing_level;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GraphIntervalConfiguration {
    private int startPercentage;
    private int endPercentage;
    private String colorCode;
}
