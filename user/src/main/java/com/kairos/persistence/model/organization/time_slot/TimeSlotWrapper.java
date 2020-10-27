package com.kairos.persistence.model.organization.time_slot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by prabjot on 11/12/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@QueryResult
@Getter
@Setter
public class TimeSlotWrapper {

    private Long id;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private boolean shiftStartTime = false;
    private String name;
    //for kpi use
    private Long unitId;
}
