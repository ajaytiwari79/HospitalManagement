package com.kairos.persistence.model.user.expertise.Response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.LocalDate;
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@QueryResult
public class ProtectedDaysOffSettingDTO {
    private Long id;
    private Long holidayId;
    private LocalDate publicHolidayDate;
    private boolean protectedDaysOff;
    private Long dayTypeId;
}
