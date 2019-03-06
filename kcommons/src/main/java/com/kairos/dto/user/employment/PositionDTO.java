package com.kairos.dto.user.employment;

import javax.validation.constraints.NotNull;

/**
 * Created by yatharth on 19/4/18.
 */

public class PositionDTO {
    @NotNull
    private String endDate;
    private Long reasonCodeId;
    private Long accessGroupIdOnPositionEnd;

    public Long getAccessGroupIdOnPositionEnd() {
        return accessGroupIdOnPositionEnd;
    }

    public void setAccessGroupIdOnPositionEnd(Long accessGroupIdOnPositionEnd) {
        this.accessGroupIdOnPositionEnd = accessGroupIdOnPositionEnd;
    }


    public Long getReasonCodeId() {
        return reasonCodeId;
    }

    public void setReasonCodeId(Long reasonCodeId) {
        this.reasonCodeId = reasonCodeId;
    }

    public String getEndDate() {
        return endDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
