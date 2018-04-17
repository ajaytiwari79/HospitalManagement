package com.kairos.activity.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.activity.persistence.enums.WTATemplateType;
import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE16
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DurationBetweenShiftsWTATemplate extends WTABaseRuleTemplate {

    private long durationBetweenShifts;
    private WTATemplateType wtaTemplateType = WTATemplateType.DURATION_BETWEEN_SHIFTS;


    public WTATemplateType getWtaTemplateType() {
        return wtaTemplateType;
    }

    public void setWtaTemplateType(WTATemplateType wtaTemplateType) {
        this.wtaTemplateType = wtaTemplateType;
    }


    public long getDurationBetweenShifts() {
        return durationBetweenShifts;
    }

    public void setDurationBetweenShifts(long durationBetweenShifts) {
        this.durationBetweenShifts = durationBetweenShifts;
    }

    public DurationBetweenShiftsWTATemplate(String name, boolean disabled,
                                            String description, long durationBetweenShifts) {
        this.name = name;
        this.disabled = disabled;
        this.description = description;
        this.durationBetweenShifts = durationBetweenShifts;

    }
    public DurationBetweenShiftsWTATemplate() {
    }
    }