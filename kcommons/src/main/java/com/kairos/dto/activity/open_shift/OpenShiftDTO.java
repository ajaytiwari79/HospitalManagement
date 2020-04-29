package com.kairos.dto.activity.open_shift;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class OpenShiftDTO {
    private BigInteger id;
    private Date startDate;
    private Date endDate;
    private Integer noOfPersonRequired;
    private List<Long> interestedStaff;
    private List<Long> declinedBy;
    private Long unitId;
    private BigInteger orderId;
    private BigInteger activityId;
    private List<Long> assignedStaff;

    public OpenShiftDTO(Date startDate, Date endDate, Integer noOfPersonRequired, List<Long> interestedStaff,
                        List<Long> declinedBy, Long unitId, BigInteger orderId, BigInteger activityId, BigInteger parentOpenShiftId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.noOfPersonRequired = noOfPersonRequired;
        this.interestedStaff = interestedStaff;
        this.declinedBy = declinedBy;
        this.unitId = unitId;
        this.orderId = orderId;
        this.activityId = activityId;
    }
}
