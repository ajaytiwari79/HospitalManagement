package com.kairos.enums.gdpr;

public enum  QuestionnaireTemplateStatus {

    DRAFT("Draft"),  PUBLISHED("Published");

    public String value;
    QuestionnaireTemplateStatus(String value) {
        this.value = value;
    }

    public static QuestionnaireTemplateStatus getQuestionnaireTemplateStatus(final String value) {

        for (QuestionnaireTemplateStatus templateStatus : QuestionnaireTemplateStatus.values()) {
            if (value.equals(templateStatus.toString())) {
                return templateStatus;
            }
        }
        return null;
    }
    }
