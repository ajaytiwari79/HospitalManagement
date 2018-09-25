package com.planner.service.shift_planning;

import com.kairos.shiftplanning.domain.Skill;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class SkillService {
    @Inject
    private ActivityMongoService activityMongoService;

    /**
     * Names used while getting Values from UnmodifiableMap
     * must be same as names used in Neo4j Query.
     * //Todo if any better alternate solution found apply
     * @param staffSkills
     * @return
     */
    public Set<Skill> setSkillsOfEmployee(Set<Map> staffSkills) {
        Set<Skill> skillSet=new HashSet<>();
       /*Iterator<Map> iterator=staffSkills.iterator();
;         while(iterator.hasNext()){*/
       for(Map  map:staffSkills){
            Skill skill=new Skill();
            skill.setId(map.get("skillId").toString());
            Object skillName=map.get("name");
            if(skillName!=null)
            {
                skill.setName((String)map.get("name"));
            }

            //skill.setSkillType(iterator.next().getSkillType());
            Object weight=map.get("weight");
            if(weight!=null) {
                skill.setWeight((Integer) map.get(weight));
            }
            skillSet.add(skill);
        }
        return skillSet;
    }
}
