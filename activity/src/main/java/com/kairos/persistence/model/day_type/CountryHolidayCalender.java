package com.kairos.persistence.model.day_type;

import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;


@Getter
@Setter
@Document
public class CountryHolidayCalender extends MongoBaseEntity {
    private String holidayTitle;
    private LocalDate holidayDate;
    private BigInteger dayTypeId;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean reOccuring;
    private String description;
    private String holidayType;
    private boolean isEnabled = true;
    private String googleCalId;
    private Long countryId;
}
