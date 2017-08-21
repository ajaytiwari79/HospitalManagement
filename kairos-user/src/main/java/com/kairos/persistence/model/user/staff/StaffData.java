package com.kairos.persistence.model.user.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by oodles on 4/1/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StaffData {
    private Long[] data;

    public Long[] getData() {
        return data;
    }

    public void setData(Long[] data) {
        this.data = data;
    }

    public StaffData(Long[] data) {
        this.data = data;
    }

    public StaffData() {
    }
}
