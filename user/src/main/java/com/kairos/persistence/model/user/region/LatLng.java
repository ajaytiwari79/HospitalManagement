package com.kairos.persistence.model.user.region;

import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by neuron on 12/6/17.
 */
@NodeEntity
@Getter
@Setter
public class LatLng extends UserBaseEntity {

    private float lat;
    private float lng;
    private int coordOrder;

    @Override
    public String toString() {
        return "LatLng{" +
                "lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
