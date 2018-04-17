package com.planning.responseDto.taskDto;

import com.planning.responseDto.commonDto.BaseDTO;
import com.planning.responseDto.skillDto.OptaSkillDTO;

import java.util.List;

public class OptaTaskTypeDTO extends BaseDTO{

    private boolean isForbiddenAllow;
    private String title;
    private List<OptaSkillDTO> optaSkills;

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

    public List<OptaSkillDTO> getOptaSkills() {
        return optaSkills;
    }

    public void setOptaSkills(List<OptaSkillDTO> optaSkills) {
        this.optaSkills = optaSkills;
    }

}
