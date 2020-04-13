package com.kairos.shiftplanning.domain.wta_ruletemplates;

import com.kairos.dto.activity.activity.activity_tabs.CutOffIntervalUnit;
import com.kairos.dto.activity.wta.AgeRange;
import com.kairos.dto.activity.wta.templates.ActivityCutOffCount;
import com.kairos.enums.wta.WTATemplateType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by pavan on 24/4/18.
 */
@Getter
@Setter
@EqualsAndHashCode
public class SeniorDaysPerYearWTATemplate extends WTABaseRuleTemplate {
    private List<AgeRange> ageRange;
    private List<BigInteger> activityIds = new ArrayList<>();
    private CutOffIntervalUnit cutOffIntervalUnit;
    private int transferLeaveCount;
    private int borrowLeaveCount;
    private float recommendedValue;
    private List<ActivityCutOffCount> activityCutOffCounts = new ArrayList<>();

    public SeniorDaysPerYearWTATemplate() {
        this.wtaTemplateType = WTATemplateType.SENIOR_DAYS_PER_YEAR;
    }

}
