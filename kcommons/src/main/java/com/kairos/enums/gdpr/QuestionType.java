package com.kairos.enums.gdpr;

public enum  QuestionType {

    TEXTBOX("Text box"),  YES_NO_MAYBE("Yes No Maybe"),MULTIPLE_CHOICE("Multi select"),SELECT_BOX("Select box");
    public String value;
    QuestionType(String value) {
        this.value = value;
    }

}
