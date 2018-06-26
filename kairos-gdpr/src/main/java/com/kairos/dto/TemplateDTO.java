package com.kairos.dto;

import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Map;

public class TemplateData {



    @NotNullOrEmpty(message = "Title cannot be empty ")
    @Pattern(message = "Numbers and Special characters are not allowed",regexp = "^[a-zA-Z\\s]+$")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*private  List<Map> template;

    public List<Map> getTemplate() {
        return template;
    }

    public void setTemplate(List<Map> template) {
        this.template = template;
    }

    public TemplateData(){}*/
}
