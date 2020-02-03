package com.kairos.dto.activity.wta.templates;

import com.kairos.commons.utils.NotNullOrEmpty;
import com.kairos.dto.activity.activity.activity_tabs.CutOffIntervalUnit;
import com.kairos.dto.activity.wta.AgeRange;
import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.enums.wta.WTATemplateType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
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
    @NotNullOrEmpty(message = "message.mismatched-ids")
    private List<BigInteger> activityIds = new ArrayList<>();
    private CutOffIntervalUnit cutOffIntervalUnit;
    protected float recommendedValue;

    public ChildCareDaysCheckWTATemplateDTO() {
        this.wtaTemplateType = WTATemplateType.CHILD_CARE_DAYS_CHECK;
    }




}
