package com.kairos.activity.response.dto.staffing_level;

import com.kairos.activity.persistence.model.staffing_level.StaffingLevelDuration;
import com.kairos.activity.persistence.model.staffing_level.StaffingLevelSetting;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.*;

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
        private StaffingLevelDuration staffingLevelDuration;

        public StaffingLevelGraphDetail() {

        }

        public StaffingLevelGraphDetail(Integer minNoOfStaff, Integer maxNoOfStaff,
                                     Integer availableNoOfStaff, StaffingLevelDuration staffingLevelDuration) {
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

        public StaffingLevelDuration getStaffingLevelDuration() {
            return staffingLevelDuration;
        }

        public void setStaffingLevelDuration(StaffingLevelDuration staffingLevelDuration) {
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
