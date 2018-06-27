package com.kairos.persistence.model.user.staff;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by prerna on 6/2/18.
 */
public class TimeCareEmploymentDTO{

    @JacksonXmlProperty
    private String Id;
    @JacksonXmlProperty
    private Date UpdateDate;
    @JacksonXmlProperty
    private String StartDate;
    @JacksonXmlProperty
    private String EndDate;
    @JacksonXmlProperty
    private String UpdateTypeFlag;
    @JacksonXmlProperty
    private String WeeklyHours;
    @JacksonXmlProperty
    private BigDecimal FullTimeHours;
    @JacksonXmlProperty
    private String WorkPlaceID;
    @JacksonXmlProperty
    private Long PersonID;
    @JacksonXmlProperty
    private String DutyCalcTypeID;
    @JacksonXmlProperty
    private Boolean UseBreak;
    @JacksonXmlProperty
    private BigDecimal MonthlyHours;
    @JacksonXmlProperty
    private String PositionId;
    @JacksonXmlProperty
    private String EmpNo;

    public TimeCareEmploymentDTO(){
        // default constructor
    }
    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public Date getUpdateDate() {
        return UpdateDate;
    }

    public void setUpdateDate(Date updateDate) {
        UpdateDate = updateDate;
    }

    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String startDate) {
        StartDate = startDate;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }

    public String getUpdateTypeFlag() {
        return UpdateTypeFlag;
    }

    public void setUpdateTypeFlag(String updateTypeFlag) {
        UpdateTypeFlag = updateTypeFlag;
    }

    public String getWeeklyHours() {
        return WeeklyHours;
    }

    public void setWeeklyHours(String weeklyHours) {
        WeeklyHours = weeklyHours;
    }

    public BigDecimal getFullTimeHours() {
        return FullTimeHours;
    }

    public void setFullTimeHours(BigDecimal fullTimeHours) {
        FullTimeHours = fullTimeHours;
    }

    public String getWorkPlaceID() {
        return WorkPlaceID;
    }

    public void setWorkPlaceID(String workPlaceID) {
        WorkPlaceID = workPlaceID;
    }

    public Long getPersonID() {
        return PersonID;
    }

    public void setPersonID(Long personID) {
        PersonID = personID;
    }

    public String getDutyCalcTypeID() {
        return DutyCalcTypeID;
    }

    public void setDutyCalcTypeID(String dutyCalcTypeID) {
        DutyCalcTypeID = dutyCalcTypeID;
    }

    public Boolean getUseBreak() {
        return UseBreak;
    }

    public void setUseBreak(Boolean useBreak) {
        UseBreak = useBreak;
    }

    public BigDecimal getMonthlyHours() {
        return MonthlyHours;
    }

    public void setMonthlyHours(BigDecimal monthlyHours) {
        MonthlyHours = monthlyHours;
    }

    public String getPositionId() {
        return PositionId;
    }

    public void setPositionId(String positionId) {
        PositionId = positionId;
    }

    public String getEmpNo() {
        return EmpNo;
    }

    public void setEmpNo(String empNo) {
        EmpNo = empNo;
    }

    /*@Override
    public int compareTo(TimeCareEmploymentDTO dto) {
        int workPlaceId=((TimeCareEmploymentDTO)dto).getWorkPlaceID();
        *//* For Ascending order*//*
        return this.studentage-compareage;

        *//* For Descending order do like this *//*
        //return compareage-this.studentage;
    }*/
}
