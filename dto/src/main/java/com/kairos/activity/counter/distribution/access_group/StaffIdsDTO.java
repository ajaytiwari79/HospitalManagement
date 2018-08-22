package com.kairos.activity.counter.distribution.access_group;

import java.util.List;

public class StaffIdsDTO {

    private Long accessGroupId;
    private List<Long> staffIds;

    public Long getAccessGroupId() {
        return accessGroupId;
    }

    public void setAccessGroupId(Long accessGroupId) {
        this.accessGroupId = accessGroupId;
    }

    public List<Long> getStaffIds() {
        return staffIds;
    }

    public void setStaffIds(List<Long> staffIds) {
        this.staffIds = staffIds;
    }
}
