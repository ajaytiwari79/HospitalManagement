package com.kairos.utils;

import java.util.List;

/**
 * Created by oodles on 13/10/16.
 */
public class SkillWrapper {
    private String text;
    private List<Object> children;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Object> getChildren() {
        return children;
    }

    public SkillWrapper() {
    }

    public void setChildren(List<Object> children) {
        this.children = children;
    }

    public SkillWrapper(String text, List<Object> children) {
        this.text = text;
        this.children = children;
    }
}
