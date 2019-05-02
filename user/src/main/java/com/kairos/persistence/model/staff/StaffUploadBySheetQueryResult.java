package com.kairos.persistence.model.staff;

import com.kairos.dto.user.staff.staff.StaffDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vipul on 4/12/17.
 */
public class StaffUploadBySheetQueryResult {
    private List<StaffDTO> staffList = new ArrayList<>();
    private List<StaffDTO> staffErrorList = new ArrayList<>();

    public StaffUploadBySheetQueryResult() {
        //Default Constructor
    }

    public List<StaffDTO> getStaffList() {
        return staffList;
    }

    public void setStaffList(List<StaffDTO> staffList) {
        this.staffList = staffList;
    }

    public List<StaffDTO> getStaffErrorList() {
        return staffErrorList;
    }

    public void setStaffErrorList(List<StaffDTO> staffErrorList) {
        this.staffErrorList = staffErrorList;
    }
}
