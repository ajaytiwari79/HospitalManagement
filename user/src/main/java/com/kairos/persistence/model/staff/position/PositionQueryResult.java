package com.kairos.persistence.model.staff.position;

import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by yatharth on 13/4/18.
 */

@QueryResult
public class PositionQueryResult {

    private Long id;
    private String name;
    private Long startDateMillis;
    private Long endDateMillis;
    private Long reasonCodeId;
    private Long accessGroupIdOnPositionEnd;


    public Long getReasonCodeId() {
        return reasonCodeId;
    }

    public void setReasonCodeId(Long reasonCodeId) {
        this.reasonCodeId = reasonCodeId;
    }



    public Long getAccessGroupIdOnPositionEnd() {
        return accessGroupIdOnPositionEnd;
    }

    public void setAccessGroupIdOnPositionEnd(Long accessGroupIdOnPositionEnd) {
        this.accessGroupIdOnPositionEnd = accessGroupIdOnPositionEnd;
    }



    public PositionQueryResult() {

    }
    public PositionQueryResult(Long id, Long startDateMillis, Long endDateMillis) {
        this.id = id;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;

    }
    public PositionQueryResult(Long id, Long startDateMillis, Long endDateMillis , Long reasonCodeId, Long accessGroupIdOnPositionEnd) {
        this.id = id;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
        this.accessGroupIdOnPositionEnd = accessGroupIdOnPositionEnd;
        this.reasonCodeId = reasonCodeId;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getStartDateMillis() {
        return startDateMillis;
    }

    public void setStartDateMillis(Long startDateMillis) {
        this.startDateMillis = startDateMillis;
    }

    public Long getEndDateMillis() {
        return endDateMillis;
    }

    public void setEndDateMillis(Long endDateMillis) {
        this.endDateMillis = endDateMillis;
    }
}
