package com.kairos.dto.user.organization;

/**
 * @author pradeep
 * @date - 12/6/18
 */

public class OrganizationSettingDTO {

    /*Walking Time setting*/
    private int walkingMeter;
    private int walkingMinutes;

    public OrganizationSettingDTO() {
    }

    public OrganizationSettingDTO(int walkingMeter, int walkingMinutes) {
        this.walkingMeter = walkingMeter;
        this.walkingMinutes = walkingMinutes;
    }

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
}
