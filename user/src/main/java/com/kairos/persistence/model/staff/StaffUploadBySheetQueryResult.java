package com.kairos.persistence.model.staff;

import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetailDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vipul on 4/12/17.
 */
public class StaffUploadBySheetQueryResult {
    List<StaffPersonalDetailDTO> staffList = new ArrayList<>();
    List<Integer> staffErrorList = new ArrayList<Integer>();

    public List<StaffPersonalDetailDTO> getStaffList() {
        return staffList;
    }

    public void setStaffList(List<StaffPersonalDetailDTO> staffList) {
        this.staffList = staffList;
    }

    public List<Integer> getStaffErrorList() {
        return staffErrorList;
    }

    public void setStaffErrorList(List<Integer> staffErrorList) {
        this.staffErrorList = staffErrorList;
    }

    public StaffUploadBySheetQueryResult() {
    }

}
