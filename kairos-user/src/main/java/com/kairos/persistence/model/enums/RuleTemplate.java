package com.kairos.persistence.model.enums;

/**
 * Created by vipul on 8/8/17.
 */
public enum RuleTemplate {
    NONE("None"), WORK_TIME("Work Time"), ALERT_TIME("Alert Time"), ON_CALL_TIME("On call time"), OVER_TIME("Overtime"), PLUS_TIME("Plus time"),
                ILLNESS_TIME("Illness time"), LEAVE_TIME("Leave time"), VACATION_TIME("Vacation time"), OTHER_TIME("Other time"), ABSENCE_TIME("absence time"),
            VETO_TIME("Veto time"), SENIOR_TIME("Senior time"), CHILD_CARE_TIME("Child-care time");
    public String value;

    RuleTemplate(String value) {
        this.value = value;
    }
    public static RuleTemplate getByValue(String value) {
        for (RuleTemplate ruleTemplate : RuleTemplate.values()) {
            if (ruleTemplate.value.equals(value)) {
                return ruleTemplate;
            }
        }
        return null;
    }

}
