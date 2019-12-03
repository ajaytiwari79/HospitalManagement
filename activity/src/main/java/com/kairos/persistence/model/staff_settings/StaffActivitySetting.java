package com.kairos.persistence.model.staff_settings;

import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class StaffActivitySetting extends MongoBaseEntity {
    private Long staffId;
    private BigInteger activityId;
    private Long employmentId;
    private Long unitId;
    private Short shortestTime;
    private Short longestTime;
    private Integer minLength;
    private Integer maxThisActivityPerShift;
    private boolean eligibleForMove;
    private LocalTime earliestStartTime;
    private LocalTime latestStartTime;
    private LocalTime maximumEndTime;
    private List<Long> dayTypeIds;
    private LocalTime defaultStartTime;

    public StaffActivitySetting(Long staffId, BigInteger activityId, Long employmentId, Long unitId,
                                Short shortestTime, Short longestTime, Integer minLength, Integer maxThisActivityPerShift,
                                boolean eligibleForMove, LocalTime earliestStartTime, LocalTime latestStartTime, LocalTime maximumEndTime, List<Long> dayTypeIds, LocalTime defaultStartTime) {
        this.staffId = staffId;
        this.activityId = activityId;
        this.employmentId = employmentId;
        this.unitId = unitId;
        this.shortestTime = shortestTime;
        this.longestTime = longestTime;
        this.minLength = minLength;
        this.maxThisActivityPerShift = maxThisActivityPerShift;
        this.eligibleForMove = eligibleForMove;
        this.earliestStartTime=earliestStartTime;
        this.latestStartTime=latestStartTime;
        this.maximumEndTime=maximumEndTime;
        this.dayTypeIds=dayTypeIds;
        this.defaultStartTime = defaultStartTime;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StaffActivitySetting that = (StaffActivitySetting) o;
        return Objects.equals(staffId, that.staffId) &&
                Objects.equals(activityId, that.activityId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(staffId, activityId);
    }
}
