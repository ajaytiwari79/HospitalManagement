package com.kairos.shiftplanning.domain.wta_ruletemplates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.activity.activity_tabs.CutOffIntervalUnit;
import com.kairos.dto.activity.wta.templates.ActivityCutOffCount;
import com.kairos.enums.wta.WTATemplateType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pavan on 23/4/18.
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ChildCareDaysCheckWTATemplate extends WTABaseRuleTemplate {
    private List<BigInteger> activityIds = new ArrayList<>();
    private float recommendedValue;
    private CutOffIntervalUnit cutOffIntervalUnit;
    private int transferLeaveCount;
    private int borrowLeaveCount;
    private List<ActivityCutOffCount> activityCutOffCounts = new ArrayList<>();


    public ChildCareDaysCheckWTATemplate() {
        this.wtaTemplateType = WTATemplateType.CHILD_CARE_DAYS_CHECK;
    }

}
