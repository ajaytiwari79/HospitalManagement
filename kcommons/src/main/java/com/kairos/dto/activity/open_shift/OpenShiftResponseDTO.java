package com.kairos.dto.activity.open_shift;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class OpenShiftResponseDTO {

    private BigInteger id;
    private LocalDate startDate;
    private LocalTime fromTime;
    private LocalTime toTime;
    private Integer noOfPersonRequired;
    private List<Long> interestedStaff;
    private List<Long> declinedBy;
    private Long unitId;
    private BigInteger orderId;
    private BigInteger activityId;
    private  LocalDate endDate;
    private List<Long> assignedStaff;
    private BigInteger parentOpenShiftId;


}
