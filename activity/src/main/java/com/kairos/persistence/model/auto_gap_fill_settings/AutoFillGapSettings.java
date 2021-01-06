package com.kairos.persistence.model.auto_gap_fill_settings;

import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.enums.auto_gap_fill_settings.AutoGapFillingScenario;
import com.kairos.enums.auto_gap_fill_settings.AutoFillGapSettingsRule;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document
public class AutoFillGapSettings extends MongoBaseEntity {
    private Long organizationTypeId;
    private Long organizationSubTypeId;
    private BigInteger phaseId;
    private LocalDate startDate;
    private LocalDate endDate;
    private AutoGapFillingScenario autoGapFillingScenario;
    private List<AutoFillGapSettingsRule> selectedAutoFillGapSettingsRules;
    private AccessGroupRole gapApplicableFor;
    private Long countryId;
    private Long unitId;
    private boolean published;
    private BigInteger parentId;
}
