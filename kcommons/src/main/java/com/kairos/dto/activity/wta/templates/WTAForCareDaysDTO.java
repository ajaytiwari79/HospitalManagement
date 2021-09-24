package com.kairos.dto.activity.wta.templates;

import com.kairos.dto.activity.activity.activity_tabs.CutOffIntervalUnit;
import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.enums.wta.WTATemplateType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pradeep
 * @date - 10/10/18
 */
@Getter
@Setter
public class WTAForCareDaysDTO extends WTABaseRuleTemplateDTO{

    @Valid
    private List<ActivityCareDayCount> careDayCounts = new ArrayList<>();
    private CutOffIntervalUnit cutOffIntervalUnit;

    public WTAForCareDaysDTO() {
        wtaTemplateType = WTATemplateType.WTA_FOR_CARE_DAYS;
    }

}
