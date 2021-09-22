package com.kairos.persistence.model.organization;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.List;

/**
 * Created by oodles on 14/9/16.
 */

@NodeEntity
public class OrganizationSetting extends UserBaseEntity {


    private static final long serialVersionUID = 8060522225022179044L;
    private List<OpeningHours> openingHour;
    private String workingDays;

    /*Walking Time setting*/
    private int walkingMeter;
    private int walkingMinutes;

    public int getWalkingMeter() {
        return walkingMeter;
    }

    public void setWalkingMeter(int walkingMeter) {
        this.walkingMeter = walkingMeter;
    }

    public int getWalkingMinutes() {
        return walkingMinutes;
    }

    public void setWalkingMinutes(int walkingMinutes) {
        this.walkingMinutes = walkingMinutes;
    }

    public List<OpeningHours> getOpeningHour() {
        return openingHour;
    }

    public void setOpeningHour(List<OpeningHours> openingHour) {
        this.openingHour = openingHour;
    }

    public String getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(String workingDays) {
        this.workingDays = workingDays;
    }


    public OrganizationSetting(List<OpeningHours> openingHours, String workingDays) {
        this.openingHour = openingHours;
        this.workingDays = workingDays;

    }

    public OrganizationSetting() {
    }


}
