package com.kairos.persistence.model.expertise;

import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpertisePublishSetting extends MongoBaseEntity {
    private Long expertiseId;
    private Map<Long,Integer> employmentTypeSettings;
    private Long unitId;
    private Long countryId;

}
