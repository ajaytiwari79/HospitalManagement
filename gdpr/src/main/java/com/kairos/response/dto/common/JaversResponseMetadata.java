package com.kairos.response.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class JaversResponseMetadata {

    private Long id;

    private String name;


    public JaversResponseMetadata(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public JaversResponseMetadata() {
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
}
