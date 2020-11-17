package com.kairos.persistence.cta_compensation_setting;

import com.kairos.dto.activity.cta_compensation_setting.CTACompensationConfiguration;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CTACompensationSetting extends MongoBaseEntity {
    private List<CTACompensationConfiguration> configurations;
    private Long countryId;
    private Long expertiseId;
    private Long unitId;
}
