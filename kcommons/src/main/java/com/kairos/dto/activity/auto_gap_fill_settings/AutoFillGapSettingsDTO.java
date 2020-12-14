package com.kairos.dto.activity.auto_gap_fill_settings;

import com.kairos.dto.activity.common.UserInfo;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.enums.auto_gap_fill_settings.AutoGapFillingScenario;
import com.kairos.enums.auto_gap_fill_settings.AutoFillGapSettingsRule;
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
public class AutoFillGapSettingsDTO {
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
    private AutoGapFillingScenario autoGapFillingScenario;
    @NotEmpty
    private Set<AutoFillGapSettingsRule> selectedAutoFillGapSettingsRules;
    private AccessGroupRole gapApplicableFor;
    private Date updatedAt;
    private UserInfo lastModifiedBy;
    private String organizationTypeName;
    private String organizationSubTypeName;
    private String phaseName;
    private boolean published;
}
