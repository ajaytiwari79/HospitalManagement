package com.kairos.persistence.model.activity.tabs;

import com.kairos.annotations.KPermissionField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by pawanmandhan on 23/8/17.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityIndividualPointsSettings {

    //method for calculating individual points
    @KPermissionField
    private String individualPointsCalculationMethod;
    @KPermissionField
    private Double numberOfFixedPoints;
}
