package com.kairos.shiftplanning.domain.wta_ruletemplates;

import com.kairos.dto.activity.activity.activity_tabs.CutOffIntervalUnit;
import com.kairos.dto.activity.wta.templates.ActivityCareDayCount;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static com.kairos.enums.wta.WTATemplateType.WTA_FOR_CARE_DAYS;


/**
 * @author pradeep
 * @date - 10/10/18
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class WTAForCareDays extends WTABaseRuleTemplate{

    private List<ActivityCareDayCount> careDayCounts = new ArrayList<>();

    private CutOffIntervalUnit cutOffIntervalUnit;

    public WTAForCareDays(String name, String description) {
        super(name, description);
    }

    public WTAForCareDays() {
        wtaTemplateType = WTA_FOR_CARE_DAYS;
    }



}
