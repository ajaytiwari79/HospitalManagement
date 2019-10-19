package com.kairos.dto.activity.staffing_level;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
public class StaffingLevelGraphDTO {
    private StaffingLevelSetting staffingLevelSetting;
    private List<Integer> minNoOfStaff=new ArrayList();
    private List <Integer>maxNoOfStaff=new ArrayList<>();
    private List<Integer> availableNoOfStaff=new ArrayList<>();


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
