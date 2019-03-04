package com.kairos.persistence.model.staff.employment;

import com.kairos.config.neo4j.converter.LocalDateConverter;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.LocalDate;

/**
 * Created by yatharth on 13/4/18.
 */

@QueryResult
public class PositionQueryResult {

    private Long id;
    private String name;
    private Long startDateMillis;
    private Long endDateMillis;
    @Convert(LocalDateConverter.class)
    private LocalDate mainEmploymentStartDate;
    @Convert(LocalDateConverter.class)
    private LocalDate mainEmploymentEndDate;
    private boolean mainEmployment;
    private Long reasonCodeId;
    private Long accessGroupIdOnEmploymentEnd;

    public PositionQueryResult(Long id, Long startDateMillis, Long endDateMillis, Long reasonCodeId, Long accessGroupIdOnEmploymentEnd, LocalDate mainEmploymentStartDate, LocalDate mainEmploymentEndDate, boolean mainEmployment) {
        this.id = id;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
        this.accessGroupIdOnEmploymentEnd = accessGroupIdOnEmploymentEnd;
        this.reasonCodeId = reasonCodeId;
        this.mainEmploymentStartDate = mainEmploymentStartDate;
        this.mainEmploymentEndDate = mainEmploymentEndDate;
        this.mainEmployment = mainEmployment;


    }

    public Long getReasonCodeId() {
        return reasonCodeId;
    }

    public void setReasonCodeId(Long reasonCodeId) {
        this.reasonCodeId = reasonCodeId;
    }



    public Long getAccessGroupIdOnEmploymentEnd() {
        return accessGroupIdOnEmploymentEnd;
    }

    public void setAccessGroupIdOnEmploymentEnd(Long accessGroupIdOnEmploymentEnd) {
        this.accessGroupIdOnEmploymentEnd = accessGroupIdOnEmploymentEnd;
    }



    public PositionQueryResult() {

    }
    public PositionQueryResult(Long id, Long startDateMillis, Long endDateMillis) {
        this.id = id;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;

    }
    public PositionQueryResult(Long id, Long startDateMillis, Long endDateMillis , Long reasonCodeId, Long accessGroupIdOnEmploymentEnd) {
        this.id = id;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
        this.accessGroupIdOnEmploymentEnd = accessGroupIdOnEmploymentEnd;
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

    public LocalDate getMainEmploymentStartDate() {
        return mainEmploymentStartDate;
    }

    public void setMainEmploymentStartDate(LocalDate mainEmploymentStartDate) {
        this.mainEmploymentStartDate = mainEmploymentStartDate;
    }

    public LocalDate getMainEmploymentEndDate() {
        return mainEmploymentEndDate;
    }

    public void setMainEmploymentEndDate(LocalDate mainEmploymentEndDate) {
        this.mainEmploymentEndDate = mainEmploymentEndDate;
    }

    public boolean isMainEmployment() {
        return mainEmployment;
    }

    public void setMainEmployment(boolean mainEmployment) {
        this.mainEmployment = mainEmployment;
    }
}
