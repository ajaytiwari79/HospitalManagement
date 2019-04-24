package com.planner.domain.task;

import com.planner.domain.common.BaseEntity;

import java.util.List;

//import org.springframework.data.cassandra.core.mapping.Table;

//@Table
public class PlanningTaskType extends BaseEntity {

    private boolean isForbiddenAllow;
    private String title;
    List<String> skillWithIds;

    public boolean isForbiddenAllow() {
        return isForbiddenAllow;
    }

    public void setForbiddenAllow(boolean forbiddenAllow) {
        isForbiddenAllow = forbiddenAllow;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getSkillWithIds() {
        return skillWithIds;
    }

    public void setSkillWithIds(List<String> skillWithIds) {
        this.skillWithIds = skillWithIds;
    }
}
