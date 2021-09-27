package com.kairos.persistence.model.wta.templates.template_types;

import com.kairos.dto.activity.activity.activity_tabs.CutOffIntervalUnit;
import com.kairos.dto.activity.wta.templates.ActivityCareDayCount;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
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
public class WTAForCareDays extends WTABaseRuleTemplate{

    private static final long serialVersionUID = 953116250236407293L;
    private List<ActivityCareDayCount> careDayCounts = new ArrayList<>();

    private CutOffIntervalUnit cutOffIntervalUnit;

    public WTAForCareDays(String name, String description) {
        super(name, description);
    }

    public WTAForCareDays() {
        wtaTemplateType = WTATemplateType.WTA_FOR_CARE_DAYS;
    }



}
