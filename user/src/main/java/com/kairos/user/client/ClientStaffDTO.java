package com.kairos.user.client;

/**
 * Created by prabjot on 25/4/17.
 */
public class ClientStaffDTO {

    private long citizenId;
    private long staffId;
    private ClientStaffRelation.StaffType type;

    public void setCitizenId(long citizenId) {
        this.citizenId = citizenId;
    }

    public void setStaffId(long staffId) {
        this.staffId = staffId;
    }

    public long getCitizenId() {

        return citizenId;
    }

    public long getStaffId() {
        return staffId;
    }

    public void setType(ClientStaffRelation.StaffType type) {
        this.type = type;
    }

    public ClientStaffRelation.StaffType getType() {

        return type;
    }
}
