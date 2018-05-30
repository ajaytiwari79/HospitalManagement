package com.kairos.activity.persistence.model.open_shift;

import com.kairos.response.dto.web.open_shift.PriorityGroupDTO;

import java.util.List;

public class OpenShiftRuleTemplateAndPriorityGroupWrapper {
    private OpenShiftRuleTemplateDTO ruleTemplates;
    private List<PriorityGroupDTO> priorityGroups;

    public OpenShiftRuleTemplateAndPriorityGroupWrapper() {
        //Default Constructor
    }

    public OpenShiftRuleTemplateAndPriorityGroupWrapper(OpenShiftRuleTemplateDTO ruleTemplates, List<PriorityGroupDTO> priorityGroups) {
        this.ruleTemplates = ruleTemplates;
        this.priorityGroups = priorityGroups;
    }

    public OpenShiftRuleTemplateDTO getRuleTemplates() {
        return ruleTemplates;
    }

    public void setRuleTemplates(OpenShiftRuleTemplateDTO ruleTemplates) {
        this.ruleTemplates = ruleTemplates;
    }

    public List<PriorityGroupDTO> getPriorityGroups() {
        return priorityGroups;
    }

    public void setPriorityGroups(List<PriorityGroupDTO> priorityGroups) {
        this.priorityGroups = priorityGroups;
    }
}
