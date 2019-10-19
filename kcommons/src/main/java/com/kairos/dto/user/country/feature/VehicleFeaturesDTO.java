package com.kairos.dto.user.country.feature;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prerna on 7/12/17.
 */
@Getter
@Setter
public class VehicleFeaturesDTO {
    private List<Long> features = new ArrayList<>();
}
