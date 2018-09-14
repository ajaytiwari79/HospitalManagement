package com.kairos.dto.user.organization.group;

public class TeamWrapper {
    private Long id;
    private String name;
    private String type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TeamWrapper(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public TeamWrapper() {
    }
}