package com.kairos.dto.user.organization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by anil on 28/7/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class TimeSlot {

    private String name;
    private Long id;
    private boolean systemGeneratedTimeSlots;
}
