package com.kairos.dto.activity.gap_settings;

import com.kairos.dto.activity.common.UserInfo;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.enums.gap_settings.GapFillingScenario;
import com.kairos.enums.gap_settings.GapSettingsRule;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class GapSettingsDTO {
    private BigInteger id;
    private Long countryId;
    private Long unitId;
    private LocalDate startDate;
    private LocalDate endDate;
    @NotNull
    private Long organizationTypeId;
    @NotNull
    private Long organizationSubTypeId;
    @NotNull
    private BigInteger phaseId;
    @NotNull
    private GapFillingScenario gapFillingScenario;
    @NotEmpty
    private Set<GapSettingsRule> selectedGapSettingsRules;
    private AccessGroupRole gapCreatedBy;
    private Date updatedAt;
    private UserInfo lastModifiedBy;
    private String organizationTypeName;
    private String organizationSubTypeName;
    private String phaseName;
}
