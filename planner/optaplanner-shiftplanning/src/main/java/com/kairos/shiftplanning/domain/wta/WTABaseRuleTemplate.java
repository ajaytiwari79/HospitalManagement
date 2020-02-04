package com.kairos.shiftplanning.domain.wta;

import com.kairos.commons.planning_setting.PlanningSetting;
import com.kairos.dto.activity.wta.templates.PhaseTemplateValue;
import com.kairos.enums.wta.WTATemplateType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.math.BigInteger;
import java.util.List;


/**
 * Created by vipul on 26/7/17.
 */

@Getter
@Setter
@NoArgsConstructor
public class WTABaseRuleTemplate{

    protected BigInteger id;
    protected String name;
    protected String description;
    protected boolean disabled;
    protected BigInteger ruleTemplateCategoryId;
    protected String lastUpdatedBy;
    protected Long countryId;
    protected WTATemplateType wtaTemplateType;
    protected List<PhaseTemplateValue> phaseTemplateValues;
    protected Integer staffCanIgnoreCounter;
    protected Integer managementCanIgnoreCounter;

    public WTABaseRuleTemplate(String name, String description) {
        this.name = name;
        this.description = description;
    }


}
