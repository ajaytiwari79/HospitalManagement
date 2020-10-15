package com.kairos.persistence.model.protected_day_off;

import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document
@Getter
@Setter
public class ProtectedDaysOffSetting extends MongoBaseEntity {
    private Long holidayId;
    private LocalDate publicHolidayDate;
    private boolean protectedDaysOff;
    private Long dayTypeId;
}
