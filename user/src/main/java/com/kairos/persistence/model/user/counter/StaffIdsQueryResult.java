package com.kairos.persistence.model.user.counter;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
public class StaffIdsQueryResult {
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
