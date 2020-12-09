package com.kairos.dto.activity.gap_settings;

import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.enums.gap_settings.GapFillingScenario;
import com.kairos.enums.gap_settings.GapSettingsRules;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class GapSettingsDTO {
    private BigInteger id;
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
