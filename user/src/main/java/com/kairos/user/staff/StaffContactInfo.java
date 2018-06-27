package com.kairos.user.staff;

/**
 * Created by prabjot on 25/10/16.
 */
public class StaffContactInfo {

    private String workEmail;
    private String workCellPhone;
    private String workTelephone;

    public StaffContactInfo(String workEmail, String workCellPhone, String workTelephone) {
        this.workEmail = workEmail;
        this.workCellPhone = workCellPhone;
        this.workTelephone = workTelephone;
    }

    public void updateContactInfo(String workEmail, String workCellPhone, String workTelephone) {
        this.workEmail = workEmail;
        this.workCellPhone = workCellPhone;
        this.workTelephone = workTelephone;
    }


}
