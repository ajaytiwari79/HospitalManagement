package com.kairos.persistence.model.break_settings;

import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document
public class BreakSettings extends MongoBaseEntity {
    private static final long serialVersionUID = 7266988493920137542L;
    private Long countryId;
    private Long shiftDurationInMinute;
    private Long breakDurationInMinute;
    private Long expertiseId;
    private BigInteger activityId;
    private boolean primary;
    private boolean includeInPlanning;

}
