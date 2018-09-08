package com.kairos.activity.control_panel;

import java.util.Map;

/**
 * Created by prabjot on 30/8/17.
 */
public class ControlPanelDTO {

    private Map<String,String> flsCredentails;
    private String jobId;
    private Long unitId;

    public Map<String, String> getFlsCredentails() {
        return flsCredentails;
    }

    public void setFlsCredentails(Map<String, String> flsCredentails) {
        this.flsCredentails = flsCredentails;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }
}
