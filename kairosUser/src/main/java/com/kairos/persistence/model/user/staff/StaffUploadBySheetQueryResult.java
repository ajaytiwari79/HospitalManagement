package com.kairos.persistence.model.user.staff;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vipul on 4/12/17.
 */
public class StaffUploadBySheetQueryResult {
    List<Staff> staffList = new ArrayList<>();
    List<Integer> staffErrorList = new ArrayList<Integer>();

    public List<Staff> getStaffList() {
        return staffList;
    }

    public void setStaffList(List<Staff> staffList) {
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

    public StaffUploadBySheetQueryResult(List<Staff> staffList, List<Integer> staffErrorList) {
        this.staffList = staffList;
        this.staffErrorList = staffErrorList;
    }
}
