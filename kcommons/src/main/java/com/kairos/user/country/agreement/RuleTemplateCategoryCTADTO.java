package com.kairos.user.country.agreement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kairos.enums.RuleTemplateCategoryType;

import java.util.List;

public class RuleTemplateCategoryCTADTO{
        @JsonProperty(value = "categoryName")
        private String name;
        private String description;
        private RuleTemplateCategoryType ruleTemplateCategoryType;
        private List<Long> ruleTemplateIds;

        public RuleTemplateCategoryCTADTO() {
                // dv
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public String getDescription() {
                return description;
        }

        public void setDescription(String description) {
                this.description = description;
        }

        public RuleTemplateCategoryType getRuleTemplateCategoryType() {
                return ruleTemplateCategoryType;
        }

        public void setRuleTemplateCategoryType(RuleTemplateCategoryType ruleTemplateCategoryType) {
                this.ruleTemplateCategoryType = ruleTemplateCategoryType;
        }

        public List<Long> getRuleTemplateIds() {
                return ruleTemplateIds;
        }

        public void setRuleTemplateIds(List<Long> ruleTemplateIds) {
                this.ruleTemplateIds = ruleTemplateIds;
        }
}
