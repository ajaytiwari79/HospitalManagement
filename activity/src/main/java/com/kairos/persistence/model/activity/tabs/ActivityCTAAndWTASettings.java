package com.kairos.persistence.model.activity.tabs;

import com.kairos.annotations.KPermissionField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by vipul on 30/11/17.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityCTAAndWTASettings {
    @KPermissionField
    private  boolean eligibleForCostCalculation;
}