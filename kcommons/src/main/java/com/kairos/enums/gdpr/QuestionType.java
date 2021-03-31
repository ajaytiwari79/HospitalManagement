package com.kairos.enums.gdpr;

import java.io.Serializable;

public enum  QuestionType implements Serializable {

    TEXTBOX("Text box"),  YES_NO_MAYBE("Yes No Maybe"),MULTIPLE_CHOICE("Multi select"),SELECT_BOX("Select box");
    public String value;
    QuestionType(String value) {
        this.value = value;
    }

}
