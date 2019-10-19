package com.kairos.dto.activity.wta.templates;

import com.kairos.dto.activity.activity.activity_tabs.CutOffIntervalUnit;
import com.kairos.dto.activity.wta.AgeRange;
import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.enums.wta.WTATemplateType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pavan on 24/4/18.
 */
@Getter
@Setter
public class ChildCareDaysCheckWTATemplateDTO extends WTABaseRuleTemplateDTO {

    private List<AgeRange> ageRange;
    private List<BigInteger> activityIds = new ArrayList<>();
    private boolean borrowLeave;
    private CutOffIntervalUnit cutOffIntervalUnit;
    protected float recommendedValue;

    public ChildCareDaysCheckWTATemplateDTO() {
        this.wtaTemplateType = WTATemplateType.CHILD_CARE_DAYS_CHECK;
    }




}
