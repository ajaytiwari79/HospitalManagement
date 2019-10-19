package com.kairos.dto.planner.vrp;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

/**
 * @author pradeep
 * @date - 28/6/18
 */

@Getter
@Setter
public class PreferedTimeWindowDTO {


    private String name;
    private Long id;
    private LocalTime fromTime;
    private LocalTime toTime;

    private String timeWindow;
}
