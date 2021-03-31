package com.kairos.persistence.model.wta.templates;

import com.kairos.commons.planning_setting.ConstraintSetting;
import com.kairos.dto.activity.wta.templates.PhaseTemplateValue;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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
public class WTABaseRuleTemplate extends MongoBaseEntity{

    private static final long serialVersionUID = 5416910411596298183L;
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
    protected ConstraintSetting constraintSetting;
    protected boolean checkRuleFromView;

    public WTABaseRuleTemplate(String name,String description) {
        this.name = name;
        this.description = description;
    }

    public void validateRules(RuleTemplateSpecificInfo infoWrapper){
        //It's being implemented
    }

    public boolean isCalculatedValueChanged(WTABaseRuleTemplate wtaBaseRuleTemplate) {
        boolean calculatedValueChanged = false;
        for (int i = 0; i < this.phaseTemplateValues.size(); i++) {
            PhaseTemplateValue thisPhaseTemplateValue = wtaBaseRuleTemplate.phaseTemplateValues.get(i);
            PhaseTemplateValue phaseTemplateValue = this.phaseTemplateValues.get(i);
            if(!thisPhaseTemplateValue.equals(phaseTemplateValue)){
                calculatedValueChanged = true;
                break;
            }
        }
        return calculatedValueChanged || !new EqualsBuilder()
                .append(this.checkRuleFromView, wtaBaseRuleTemplate.checkRuleFromView)
                .append(this.staffCanIgnoreCounter, wtaBaseRuleTemplate.staffCanIgnoreCounter)
                .append(this.managementCanIgnoreCounter, wtaBaseRuleTemplate.managementCanIgnoreCounter)
                .append(this.constraintSetting, wtaBaseRuleTemplate.constraintSetting)
                .isEquals();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        WTABaseRuleTemplate that = (WTABaseRuleTemplate) o;

        return new EqualsBuilder()
                .append(checkRuleFromView, that.checkRuleFromView)
                .append(phaseTemplateValues, that.phaseTemplateValues)
                .append(staffCanIgnoreCounter, that.staffCanIgnoreCounter)
                .append(managementCanIgnoreCounter, that.managementCanIgnoreCounter)
                .append(constraintSetting, that.constraintSetting)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(phaseTemplateValues)
                .append(staffCanIgnoreCounter)
                .append(managementCanIgnoreCounter)
                .append(constraintSetting)
                .append(checkRuleFromView)
                .toHashCode();
    }
}
