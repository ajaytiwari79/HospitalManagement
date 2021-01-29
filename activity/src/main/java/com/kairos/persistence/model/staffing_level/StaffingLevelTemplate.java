package com.kairos.persistence.model.staffing_level;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.staffing_level.StaffingLevelInterval;
import com.kairos.dto.activity.staffing_level.StaffingLevelSetting;
import com.kairos.dto.activity.staffing_level.StaffingLevelTemplatePeriod;
import com.kairos.enums.Day;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@Document(collection = "staffing_level_template")
public class StaffingLevelTemplate extends MongoBaseEntity{
    private String name;
    private Long unitId;
    private StaffingLevelTemplatePeriod validity;
    private Set<BigInteger> dayType=new HashSet<>();
    private List<Day> validDays =new ArrayList<>();
    private StaffingLevelSetting staffingLevelSetting;
    private List<StaffingLevelInterval> presenceStaffingLevelInterval =new ArrayList<>();
    private boolean disabled;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof StaffingLevelTemplate)) return false;

        StaffingLevelTemplate that = (StaffingLevelTemplate) o;

        return new EqualsBuilder()
                .append(name, that.name)
                .append(validity, that.validity)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(validity)
                .toHashCode();
    }
}
