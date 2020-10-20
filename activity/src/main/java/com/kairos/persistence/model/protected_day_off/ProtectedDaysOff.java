package com.kairos.persistence.model.protected_day_off;

import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.time.LocalDate;

@Document
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProtectedDaysOff extends MongoBaseEntity {
    private BigInteger holidayId;
    private LocalDate publicHolidayDate;
    private boolean protectedDaysOff;
    private BigInteger dayTypeId;
    private Long expertiseId;
}
