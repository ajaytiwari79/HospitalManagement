package com.kairos.dto.user.organization.group;

import java.util.List;

public class GroupWrapper {
    private Long id;
    private String name;
    private String type;
    private List<TeamWrapper> teamList;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }


    public List<TeamWrapper> getTeamList() {
        return teamList;
    }

    public void setTeamList(List<TeamWrapper> teamList) {
        this.teamList = teamList;
    }

    public GroupWrapper() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public GroupWrapper(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}