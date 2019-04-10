package com.kairos.persistence.model.staff.position;

import com.kairos.persistence.model.user.unit_position.query_result.EmploymentQueryResult;

import java.util.List;

/**
 * Created by yatharth on 13/4/18.
 */
public class EmploymentAndPositionDTO {

    private PositionQueryResult position;
    private List<EmploymentQueryResult> employments;

    public PositionQueryResult getPosition() {
        return position;
    }

    public EmploymentAndPositionDTO() {

    }

    public EmploymentAndPositionDTO(PositionQueryResult position, List<EmploymentQueryResult> employments) {
        this.position = position;
        this.employments = employments;

    }

    public void setPosition(PositionQueryResult position) {
        this.position = position;
    }

    public List<EmploymentQueryResult> getEmployments() {
        return employments;
    }

    public void setEmployments(List<EmploymentQueryResult> employments) {
        this.employments = employments;
    }



}
