package com.kairos.dto.activity.wta.templates;

import com.kairos.commons.utils.NotNullOrEmpty;
import com.kairos.dto.activity.activity.activity_tabs.CutOffIntervalUnit;
import com.kairos.dto.activity.wta.AgeRange;
import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.enums.wta.WTATemplateType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by pavan on 24/4/18.
 */
@Getter
@Setter
public class SeniorDaysPerYearWTATemplateDTO extends WTABaseRuleTemplateDTO {
    private List<AgeRange> ageRange;
    @NotNullOrEmpty(message = "message.mismatched-ids")
    private List<BigInteger> activityIds;
    private CutOffIntervalUnit cutOffIntervalUnit;
    public float getRecommendedValue() {
        return recommendedValue;
    }

    public void setRecommendedValue(float recommendedValue) {
        this.recommendedValue = recommendedValue;
    }

    protected float recommendedValue;
    public SeniorDaysPerYearWTATemplateDTO() {
        this.wtaTemplateType = WTATemplateType.SENIOR_DAYS_PER_YEAR;
    }
}
