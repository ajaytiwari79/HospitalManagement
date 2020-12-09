package com.kairos.persistence.model.gap_settings;

import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.enums.gap_settings.GapFillingScenario;
import com.kairos.enums.gap_settings.GapSettingsRules;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document
public class GapSettings extends MongoBaseEntity {
    private Long organizationTypeId;
    private Long organizationSubTypeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigInteger phaseId;
    private GapFillingScenario gapFillingScenario;
    private Set<GapSettingsRules> selectedGapSettingsRules;
    private AccessGroupRole actionMadeBy;
    private Long countryId;
    private Long unitId;
}
