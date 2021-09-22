package com.kairos.persistence.model.activity.tabs;

import com.kairos.annotations.KPermissionField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by pawanmandhan on 23/8/17.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityIndividualPointsSettings implements Serializable {

    private static final long serialVersionUID = 5441972196335411203L;
    //method for calculating individual points
    @KPermissionField
    private String individualPointsCalculationMethod;
    @KPermissionField
    private Double numberOfFixedPoints;
}
