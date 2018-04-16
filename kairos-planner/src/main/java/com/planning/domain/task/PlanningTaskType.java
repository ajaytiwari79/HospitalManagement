package com.planning.domain.task;

import com.planning.domain.common.BaseEntity;
import org.springframework.data.cassandra.mapping.Table;

import java.util.List;

@Table
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
