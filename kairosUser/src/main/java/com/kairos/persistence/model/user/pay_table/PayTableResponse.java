package com.kairos.persistence.model.user.pay_table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.organization.Level;
import org.neo4j.ogm.annotation.typeconversion.DateLong;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by vipul on 15/3/18.
 */
@QueryResult
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayTableResponse implements Comparable<PayTableResponse> {
    private Long id;
    private String name;
    private String shortName;
    private Long startDateMillis;
    private Long endDateMillis;
    private String paymentUnit;
    private Level level;
    private List<PayGrade> payGrades;
    private String description;
    private Boolean published;
    private Boolean editable;


    public PayTableResponse() {
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

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
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

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public List<PayGrade> getPayGrades() {
        return payGrades;
    }

    public void setPayGrades(List<PayGrade> payGrades) {
        this.payGrades = payGrades;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public PayTableResponse(String name, String shortName, String description, Long startDateMillis, Long endDateMillis, Boolean published, String paymentUnit, Boolean editable) {
        this.name = name;
        this.published = published;
        this.description = description;
        this.shortName = shortName;
        this.startDateMillis = startDateMillis;
        this.editable = editable;
        this.endDateMillis = endDateMillis;
        this.paymentUnit = paymentUnit;


    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PayTableQueryResult{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", shortName='").append(shortName).append('\'');
        sb.append(", startDateMillis=").append(startDateMillis);
        sb.append(", endDateMillis=").append(endDateMillis);
        sb.append(", description='").append(description).append('\'');
        sb.append('}');
        return sb.toString();
    }


    @Override
    public int compareTo(PayTableResponse payTableResponse) {
        Long compareQuantity = ((PayTableResponse) payTableResponse).getStartDateMillis();
        //ascending order
        return this.getStartDateMillis().compareTo(compareQuantity);
    }

    public String getPaymentUnit() {
        return paymentUnit;
    }

    public void setPaymentUnit(String paymentUnit) {
        this.paymentUnit = paymentUnit;
    }
}
