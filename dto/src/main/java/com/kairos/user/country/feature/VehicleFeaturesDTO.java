package com.kairos.user.country.feature;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prerna on 7/12/17.
 */
public class VehicleFeaturesDTO {
    private List<Long> features = new ArrayList<>();

    public List<Long> getFeatures() {
        return features;
    }

    public void setFeatures(List<Long> features) {
        this.features = features;
    }

    public VehicleFeaturesDTO(){
        // default constructor
    }
}
