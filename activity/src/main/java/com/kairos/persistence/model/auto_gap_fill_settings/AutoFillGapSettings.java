package com.kairos.persistence.model.auto_gap_fill_settings;

import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.enums.auto_gap_fill_settings.AutoFillGapSettingsRule;
import com.kairos.enums.auto_gap_fill_settings.AutoGapFillingScenario;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.LinkedHashSet;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document
public class AutoFillGapSettings extends MongoBaseEntity {
    private static final long serialVersionUID = 7544736561082188532L;
    private Long organizationTypeId;
    private Long organizationSubTypeId;
    private BigInteger phaseId;
    private LocalDate startDate;
    private LocalDate endDate;
    private AutoGapFillingScenario autoGapFillingScenario;
    private LinkedHashSet<AutoFillGapSettingsRule> selectedAutoFillGapSettingsRules;
    private AccessGroupRole gapApplicableFor;
    private Long countryId;
    private Long unitId;
    private boolean published;
    private BigInteger parentId;
}
