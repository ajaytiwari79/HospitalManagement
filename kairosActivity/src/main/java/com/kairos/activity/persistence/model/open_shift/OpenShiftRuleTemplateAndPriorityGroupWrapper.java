package com.kairos.activity.persistence.model.open_shift;

import com.kairos.response.dto.web.open_shift.PriorityGroupDTO;

import java.util.List;

public class OpenShiftRuleTemplateAndPriorityGroupWrapper {
    private OpenShiftRuleTemplateDTO ruleTemplate;
    private List<PriorityGroupDTO> priorityGroups;

    public OpenShiftRuleTemplateAndPriorityGroupWrapper() {
        //Default Constructor
    }

    public OpenShiftRuleTemplateAndPriorityGroupWrapper(OpenShiftRuleTemplateDTO ruleTemplate, List<PriorityGroupDTO> priorityGroups) {
        this.ruleTemplate = ruleTemplate;
        this.priorityGroups = priorityGroups;
    }

    public OpenShiftRuleTemplateDTO getRuleTemplate() {
        return ruleTemplate;
    }

    public void setRuleTemplate(OpenShiftRuleTemplateDTO ruleTemplate) {
        this.ruleTemplate = ruleTemplate;
    }

    public List<PriorityGroupDTO> getPriorityGroups() {
        return priorityGroups;
    }

    public void setPriorityGroups(List<PriorityGroupDTO> priorityGroups) {
        this.priorityGroups = priorityGroups;
    }
}
