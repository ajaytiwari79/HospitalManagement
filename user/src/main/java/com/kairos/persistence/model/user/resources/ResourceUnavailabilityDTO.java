package com.kairos.persistence.model.user.resources;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created by prabjot on 25/10/17.
 */
@Getter
@Setter
public class ResourceUnavailabilityDTO {
    private List<String> unavailabilityDates;
    private String startTime;
    private String endTime;
    private boolean fullDay;
}
