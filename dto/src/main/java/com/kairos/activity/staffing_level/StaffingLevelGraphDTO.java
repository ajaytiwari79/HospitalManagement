package com.kairos.activity.staffing_level;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

public class StaffingLevelGraphDTO {
    private StaffingLevelSetting staffingLevelSetting;
    private List<Integer> minNoOfStaff=new ArrayList();
    private List <Integer>maxNoOfStaff=new ArrayList<>();
    private List<Integer> availableNoOfStaff=new ArrayList<>();


    public StaffingLevelGraphDTO() {
    }

    public StaffingLevelSetting getStaffingLevelSetting() {
        return staffingLevelSetting;
    }

    public void setStaffingLevelSetting(StaffingLevelSetting staffingLevelSetting) {
        this.staffingLevelSetting = staffingLevelSetting;
    }

    public class StaffingLevelGraphDetail{
        private Integer minNoOfStaff;
        private Integer maxNoOfStaff;
        private Integer availableNoOfStaff;
        private Duration staffingLevelDuration;

        public StaffingLevelGraphDetail() {

        }

        public StaffingLevelGraphDetail(Integer minNoOfStaff, Integer maxNoOfStaff,
                                     Integer availableNoOfStaff, Duration staffingLevelDuration) {
            this.minNoOfStaff = minNoOfStaff;
            this.maxNoOfStaff = maxNoOfStaff;
            this.availableNoOfStaff = availableNoOfStaff;
            this.staffingLevelDuration = staffingLevelDuration;
        }

        public Integer getMinNoOfStaff() {
            return minNoOfStaff;
        }

        public void setMinNoOfStaff(Integer minNoOfStaff) {
            this.minNoOfStaff = minNoOfStaff;
        }

        public Integer getMaxNoOfStaff() {
            return maxNoOfStaff;
        }

        public void setMaxNoOfStaff(Integer maxNoOfStaff) {
            this.maxNoOfStaff = maxNoOfStaff;
        }

        public Integer getAvailableNoOfStaff() {
            return availableNoOfStaff;
        }

        public void setAvailableNoOfStaff(Integer availableNoOfStaff) {
            this.availableNoOfStaff = availableNoOfStaff;
        }

        public Duration getStaffingLevelDuration() {
            return staffingLevelDuration;
        }

        public void setStaffingLevelDuration(Duration staffingLevelDuration) {
            this.staffingLevelDuration = staffingLevelDuration;
        }

    }

    public List<Integer> getMinNoOfStaff() {
        return minNoOfStaff;
    }

    public List<Integer> getMaxNoOfStaff() {
        return maxNoOfStaff;
    }

    public List<Integer> getAvailableNoOfStaff() {
        return availableNoOfStaff;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("staffingLevelSetting", staffingLevelSetting)
                .append("minNoOfStaff", minNoOfStaff)
                .append("maxNoOfStaff", maxNoOfStaff)
                .append("availableNoOfStaff", availableNoOfStaff)
                .toString();
    }
}
