package com.kairos.enums.gdpr;

import java.io.Serializable;

public enum  QuestionnaireTemplateStatus implements Serializable {

    DRAFT("Draft"),  PUBLISHED("Published");

    public String value;
    QuestionnaireTemplateStatus(String value) {
        this.value = value;
    }

    }
