package com.kairos.persistence.model.activity.tabs;

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
public class CTAAndWTASettingsActivityTab implements Serializable{
    private  boolean eligibleForCostCalculation;
}
