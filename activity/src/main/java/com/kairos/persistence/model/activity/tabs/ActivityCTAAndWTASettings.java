package com.kairos.persistence.model.activity.tabs;

import com.kairos.annotations.KPermissionField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by vipul on 30/11/17.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityCTAAndWTASettings implements Serializable {
    private static final long serialVersionUID = -3739850098409906505L;
    @KPermissionField
    private  boolean eligibleForCostCalculation;
}
