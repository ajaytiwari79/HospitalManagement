package com.kairos.persistence.model.user.resources;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by arvind on 6/10/16.
 */

@NodeEntity
public class ResourceUnAvailability extends UserBaseEntity {


    private long notAvailableFrom;
    private long notAvailableTo;

    public long getNotAvailableFrom() {
        return notAvailableFrom;
    }

    public void setNotAvailableFrom(long notAvailableFrom) {
        this.notAvailableFrom = notAvailableFrom;
    }

    public long getNotAvailableTo() {
        return notAvailableTo;
    }

    public void setNotAvailableTo(long notAvailableTo) {
        this.notAvailableTo = notAvailableTo;
    }

    public ResourceUnAvailability(long notAvailableFrom, long notAvailableTo) {
        this.notAvailableFrom = notAvailableFrom;
        this.notAvailableTo = notAvailableTo;
    }

    public ResourceUnAvailability() {
    }


}
