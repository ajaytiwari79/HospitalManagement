package com.planner.service.shift_planning;

import com.kairos.shiftplanning.domain.Skill;
import com.planner.domain.query_results.SkillQueryResult;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

@Service
public class SkillService {
    @Inject
    private ActivityMongoService activityMongoService;


    public Set<Skill> setSkillsOfEmployee(Set<SkillQueryResult> staffSkills) {
        Set<Skill> skillSet=new HashSet<>();
        for(SkillQueryResult skillQueryResult:staffSkills){
            Skill skill=new Skill();
            skill.setId(skillQueryResult.getId());
            skill.setName(skillQueryResult.getName());
            skill.setSkillType(skillQueryResult.getSkillType());
            skill.setWeight(skillQueryResult.getWeight());
            skillSet.add(skill);
        }
        return skillSet;
    }
}
