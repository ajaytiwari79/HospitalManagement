package com.kairos.persistence.model.activity.tabs;

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
public class IndividualPointsActivityTab {

    //method for calculating individual points
    private String individualPointsCalculationMethod;
    private Double numberOfFixedPoints;
}
