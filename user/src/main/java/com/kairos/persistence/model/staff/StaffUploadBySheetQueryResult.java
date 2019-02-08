package com.kairos.persistence.model.staff;

import com.kairos.dto.user.staff.staff.StaffDTO;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetailDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vipul on 4/12/17.
 */
public class StaffUploadBySheetQueryResult {
    List<StaffDTO> staffList = new ArrayList<>();
    List<StaffDTO> staffErrorList = new ArrayList<>();

    public StaffUploadBySheetQueryResult() {
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
