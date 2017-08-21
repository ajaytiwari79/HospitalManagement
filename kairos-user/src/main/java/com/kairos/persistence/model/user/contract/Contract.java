package com.kairos.persistence.model.user.contract;

import org.neo4j.ogm.annotation.NodeEntity;

import com.kairos.persistence.model.common.UserBaseEntity;

/**
 * Created by oodles on 23/11/16.
 */
@NodeEntity
public class Contract extends UserBaseEntity{

    private String name;
    private String level;
    private boolean agreed;
    private String standardWorkingHours;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public boolean isAgreed() {
        return agreed;
    }

    public void setAgreed(boolean agreed) {
        this.agreed = agreed;
    }

    public String getStandardWorkingHours() {
        return standardWorkingHours;
    }

    public void setStandardWorkingHours(String standardWorkingHours) {
        this.standardWorkingHours = standardWorkingHours;
    }

    public Contract() {
    }
}
