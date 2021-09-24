package com.kairos.persistence.model.attendence_setting;

import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.time.LocalDate;

/**
 * CreatedBy vipulpandey on 4/9/18
 **/
@Document
@Getter
@Setter
@NoArgsConstructor
public class SickSettings extends MongoBaseEntity {
    private Long staffId;
    private Long unitId;
    private Long userId;
    private BigInteger activityId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long employmentId;   // This is required to find user shifts based on employment Id



    public SickSettings(Long staffId, Long unitId, Long userId, BigInteger activityId, LocalDate startDate,Long employmentId) {
        this.staffId = staffId;
        this.unitId = unitId;
        this.userId = userId;
        this.activityId = activityId;
        this.startDate = startDate;
        this.employmentId = employmentId;
    }


}
