package com.kairos.persistence.model.user.agreement.wta.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by pawanmandhan on 26/7/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
public class WTABaseRuleTemplate extends UserBaseEntity {

    protected String name;
    protected String templateType;
    protected boolean isActive=true;
    protected String description;
    protected RuleTemplateCategory ruleTemplateCategory;

    public RuleTemplateCategory getRuleTemplateCategory() {
        return ruleTemplateCategory;
    }

    public void setRuleTemplateCategory(RuleTemplateCategory ruleTemplateCategory) {
        this.ruleTemplateCategory = ruleTemplateCategory;
    }
    //
  /* protected String time;
    protected List<String> balanceType;//multiple check boxes
    protected boolean checkAgainstTimeRules;
    protected long days;//no of days
    protected String minimumRest;//hh:mm
    protected long daysWorked;
    protected long nightsWorked;      // corrected Spellings
    protected long interval;//
    protected String intervalUnit;
    protected long validationStartDate;
    protected long minimumDaysOff;
    protected long maximumVeto;
    protected long numberShiftsPerPeriod;
    protected long numberOfWeeks;
    protected String fromDayOfWeek; //(day of week)
    protected long fromTime;
    protected long proportional;
    protected String toDayOfWeek;
    protected long toTime;// (number)
    protected long continuousDayRestHours;// (number)         // corrected Spelling
    protected long minimumDurationBetweenShifts ;//hours(number)
    protected long continuousWeekRest;//(hours number)
    protected long averageRest;//(hours number)
    protected List<String> shiftAffiliation;//(List checkbox)
    protected long number;
    protected boolean onlyCompositeShifts;//(checkbox)
    protected List<String> activityType;// checkbox)
*/

    public WTABaseRuleTemplate(){}

    public WTABaseRuleTemplate(String name, String templateType, String description) {
        this.name = name;
        this.templateType = templateType;
        this.description = description;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }


    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "WTABaseRuleTemplate{" +
                "name='" + name + '\'' +
                ", templateType='" + templateType + '\'' +
                 ", isActive=" + isActive +
                ", description='" + description + '\'' +
                '}';
    }
}
