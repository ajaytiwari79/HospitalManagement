package com.kairos.enums;

public enum  QuestionType {

    TEXTBOX("text_box"),  YES_NO_MAYBE("yes_no_maybe"),MULTIPLE_CHOICE("multi select");
    public String value;
    QuestionType(String value) {
        this.value = value;
    }

}
