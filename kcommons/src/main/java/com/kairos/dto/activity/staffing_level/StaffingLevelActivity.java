package com.kairos.dto.activity.staffing_level;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigInteger;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class StaffingLevelActivity {

    private String name;
    private BigInteger activityId;
    private boolean includeInMin;
    private int minNoOfStaff;
    private int maxNoOfStaff;
    private int availableNoOfStaff;


    public StaffingLevelActivity(String name, int minNoOfStaff, int maxNoOfStaff) {
        this.name = name;
        this.minNoOfStaff = minNoOfStaff;
        this.maxNoOfStaff = maxNoOfStaff;
    }

    public StaffingLevelActivity(BigInteger activityId, String name, int minNoOfStaff, int maxNoOfStaff) {
        this.activityId = activityId;
        this.name = name;
        this.minNoOfStaff = minNoOfStaff;
        this.maxNoOfStaff = maxNoOfStaff;
    }

    public StaffingLevelActivity(BigInteger activityId, int minNoOfStaff, int maxNoOfStaff) {
        this.activityId = activityId;
        this.minNoOfStaff = minNoOfStaff;
        this.maxNoOfStaff = maxNoOfStaff;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof StaffingLevelActivity)) return false;

        StaffingLevelActivity that = (StaffingLevelActivity) o;

        return new EqualsBuilder()
                .append(activityId, that.activityId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hash(activityId);
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("activityId", activityId)
                .toString();
    }

}
