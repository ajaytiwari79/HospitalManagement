package com.kairos.shiftplanning.domain.wta_ruletemplates;

import com.kairos.commons.planning_setting.PlanningSetting;
import com.kairos.dto.activity.wta.templates.PhaseTemplateValue;
import com.kairos.enums.constraint.ScoreLevel;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.shiftplanning.constraints.Constraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.List;


/**
 * Created by vipul on 26/7/17.
 */
@Getter
@Setter
@Document(collection = "wtaBaseRuleTemplate")
@NoArgsConstructor
public class WTABaseRuleTemplate implements Constraint {

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
    protected PlanningSetting planningSetting;
    protected boolean checkRuleFromView;

    public WTABaseRuleTemplate(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public boolean isCalculatedValueChanged(WTABaseRuleTemplate wtaBaseRuleTemplate) {
        return false;
    }


    @Override
    public ScoreLevel getLevel() {
        return this.planningSetting.getScoreLevel();
    }

    @Override
    public int getWeight() {
        return this.planningSetting.getConstraintWeight();
    }
}
