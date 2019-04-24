package com.kairos.dto.activity.wta.templates;

import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.enums.wta.WTATemplateType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pradeep
 * @date - 10/10/18
 */

public class WTAForCareDaysDTO extends WTABaseRuleTemplateDTO{


    public WTAForCareDaysDTO(String name, String description) {
        super(name, description);
    }

    public WTAForCareDaysDTO() {
        wtaTemplateType = WTATemplateType.WTA_FOR_CARE_DAYS;
    }

    private List<ActivityCareDayCount> careDayCounts = new ArrayList<>();

    public List<ActivityCareDayCount> getCareDayCounts() {
        return careDayCounts;
    }

    public void setCareDayCounts(List<ActivityCareDayCount> careDayCounts) {
        this.careDayCounts = careDayCounts;
    }
}
