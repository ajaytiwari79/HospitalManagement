package com.kairos.persistence.model.user.staff;

import com.kairos.persistence.model.user.staff.StaffPersonalDetailDTO;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.ArrayList;
import java.util.List;

@QueryResult
public class UnitStaffQueryResult {

    private Long unitId;
    private List<StaffPersonalDetailDTO> staffList = new ArrayList<>();

    public UnitStaffQueryResult(){
        // default constructor
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public List<StaffPersonalDetailDTO> getStaffList() {
        return staffList;
    }

    public void setStaffList(List<StaffPersonalDetailDTO> staffList) {
        this.staffList = staffList;
    }
}
