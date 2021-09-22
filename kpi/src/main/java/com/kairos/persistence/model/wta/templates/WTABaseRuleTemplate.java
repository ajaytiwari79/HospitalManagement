package com.kairos.persistence.model.wta.templates;

import com.kairos.commons.planning_setting.ConstraintSetting;
import com.kairos.dto.activity.wta.templates.PhaseTemplateValue;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.MongoBaseEntity;
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
public class WTABaseRuleTemplate extends MongoBaseEntity {

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
}
