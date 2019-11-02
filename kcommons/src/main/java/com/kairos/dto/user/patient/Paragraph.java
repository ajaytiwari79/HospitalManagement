package com.kairos.dto.user.patient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class Paragraph {
    private String id;

    private String description;

    private String name;

    private String active;

    private ParagraphGroup group;

    private String section;

    private String version;

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", description = "+description+", name = "+name+", active = "+active+", group = "+group+", section = "+section+", version = "+version+"]";
    }
}