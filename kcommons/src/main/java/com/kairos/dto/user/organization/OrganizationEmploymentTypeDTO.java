package com.kairos.dto.user.organization;

import com.kairos.enums.shift.PaidOutFrequencyEnum;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * Created by prerna on 8/11/17.
 */
@Getter
@Setter
public class OrganizationEmploymentTypeDTO {
    private long employmentTypeId;
    private boolean allowedForContactPerson;
    private boolean allowedForShiftPlan;
    private boolean allowedForFlexPool;
    @NotNull(message = "Payment Frequency can't be null")
    private PaidOutFrequencyEnum paymentFrequency;

}
