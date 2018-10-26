package com.kairos.enums.gdpr;

public enum  QuestionnaireTemplateStatus {

    DRAFT("Draft"),  PUBLISHED("Published");

    public String value;
    QuestionnaireTemplateStatus(String value) {
        this.value = value;
    }

    }
