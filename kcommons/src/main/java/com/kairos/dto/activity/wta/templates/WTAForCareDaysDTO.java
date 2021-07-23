package com.kairos.dto.activity.wta.templates;

import com.kairos.dto.activity.activity.activity_tabs.CutOffIntervalUnit;
import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.enums.wta.WTATemplateType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public Map<BigInteger,ActivityCareDayCount> careDaysCountMap(){
        return this.careDayCounts.stream().collect(Collectors.toMap(ActivityCareDayCount::getActivityId, v->v));
    }

}
