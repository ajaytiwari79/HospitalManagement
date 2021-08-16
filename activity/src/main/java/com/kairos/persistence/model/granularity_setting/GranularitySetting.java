package com.kairos.persistence.model.granularity_setting;

import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document
public class GranularitySetting extends MongoBaseEntity {
    private int granularityInMinute;
    private Long countryId;
    private Long organisationTypeId;
    private Long unitId;
    private LocalDate startDate;
    private LocalDate endDate;

    public GranularitySetting(Long unitId, int granularityInMinute, LocalDate startDate, LocalDate endDate) {
        this.unitId = unitId;
        this.granularityInMinute = granularityInMinute;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public GranularitySetting(Long countryId, int granularityInMinute, Long organisationTypeId) {
        this.countryId = countryId;
        this.granularityInMinute = granularityInMinute;
        this.organisationTypeId = organisationTypeId;
    }
}
