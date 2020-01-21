package com.kairos.persistence.model.staff;

import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vipul on 4/12/17.
 */
public class StaffUploadBySheetQueryResult {
    private List<StaffPersonalDetail> staffList = new ArrayList<>();
    private List<StaffPersonalDetail> staffErrorList = new ArrayList<>();

    public StaffUploadBySheetQueryResult() {
        //Default Constructor
    }

    public List<StaffPersonalDetail> getStaffList() {
        return staffList;
    }

    public void setStaffList(List<StaffPersonalDetail> staffList) {
        this.staffList = staffList;
    }

    public List<StaffPersonalDetail> getStaffErrorList() {
        return staffErrorList;
    }

    public void setStaffErrorList(List<StaffPersonalDetail> staffErrorList) {
        this.staffErrorList = staffErrorList;
    }
}
