package com.kairos.persistence.model.query_wrapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.LocalDate;

/**
 * Created by oodles on 16/11/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@QueryResult
@Getter
@Setter
public class CountryHolidayCalendarQueryResult {

    private LocalDate holidayDate;
    private DayTypeDTO dayType;
    private Long id;
    private String holidayTitle;
    private String startTime;
    private String endTime;
    private boolean reOccuring;
    private String description;
    private boolean holidayType;
    private boolean isEnabled = true;
    private String googleCalId;
    private Long dayTypeId;
    private Long countryId;


}
