package com.kairos.enums;

public enum  QuestionType {

    TEXT("questionType1"),  YES_NO_MAYBE("Yes No Maybe");
    public String value;
    QuestionType(String value) {
        this.value = value;
    }

}
