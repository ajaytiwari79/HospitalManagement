package com.kairos.persistence.model.user.expertise.Response;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * @author pradeep
 * @date - 30/10/18
 */
@QueryResult
public class SeniorAndChildCareDaysQueryResult {
    private List<CareDaysQueryResult> seniorDays;
    private List<CareDaysQueryResult> childCareDays;

    public List<CareDaysQueryResult> getSeniorDays() {
        return seniorDays;
    }

    public void setSeniorDays(List<CareDaysQueryResult> seniorDays) {
        this.seniorDays = seniorDays;
    }

    public List<CareDaysQueryResult> getChildCareDays() {
        return childCareDays;
    }

    public void setChildCareDays(List<CareDaysQueryResult> childCareDays) {
        this.childCareDays = childCareDays;
    }
}
