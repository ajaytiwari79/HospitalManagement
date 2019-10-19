package com.kairos.dto.activity.wta.templates;

import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.enums.wta.WTATemplateType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pradeep
 * @date - 10/10/18
 */
@Getter
@Setter
public class WTAForCareDaysDTO extends WTABaseRuleTemplateDTO{

    private List<ActivityCareDayCount> careDayCounts = new ArrayList<>();

}
