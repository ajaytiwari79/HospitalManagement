package com.kairos.dto.activity.time_bank.time_bank_basic.time_bank;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/*
* Created By Pradeep singh rajawat
*  Date-27/01/2018
*
* */
public class ScheduleTimeByTimeTypeDTO {

    private int totalMin;
    private BigInteger timeTypeId;
    private String label;


    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    private List<ScheduleTimeByTimeTypeDTO> children = new ArrayList();

    public ScheduleTimeByTimeTypeDTO(int totalMin) {
        this.totalMin = totalMin;
    }

    public ScheduleTimeByTimeTypeDTO(BigInteger timeTypeId) {
        this.timeTypeId = timeTypeId;
    }

    public BigInteger getTimeTypeId() {
        return timeTypeId;
    }

    public void setTimeTypeId(BigInteger timeTypeId) {
        this.timeTypeId = timeTypeId;
    }

    public int getTotalMin() {
        return totalMin;
    }

    public void setTotalMin(int totalMin) {
        this.totalMin = totalMin;
    }

    public List<ScheduleTimeByTimeTypeDTO> getChildren() {
        return children;
    }

    public void setChildren(List<ScheduleTimeByTimeTypeDTO> children) {
        this.children = children;
    }

}
