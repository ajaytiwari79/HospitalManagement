package com.kairos.persistence.model.wta.templates.template_types;

import com.kairos.dto.activity.wta.templates.ActivityCareDayCount;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;

import java.util.List;

/**
 * @author pradeep
 * @date - 10/10/18
 */

public class WTAForCareDays extends WTABaseRuleTemplate{

    private List<ActivityCareDayCount> careDayCounts;

    public WTAForCareDays(String name, String description) {
        super(name, description);
    }

    public WTAForCareDays() {
        wtaTemplateType = WTATemplateType.WTA_FOR_CARE_DAYS;
    }

    public List<ActivityCareDayCount> getCareDayCounts() {
        return careDayCounts;
    }

    public void setCareDayCounts(List<ActivityCareDayCount> careDayCounts) {
        this.careDayCounts = careDayCounts;
    }
}
