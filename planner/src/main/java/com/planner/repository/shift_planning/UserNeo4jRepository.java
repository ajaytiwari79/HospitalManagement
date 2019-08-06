package com.planner.repository.shift_planning;

import com.kairos.commons.utils.ObjectMapperUtils;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.*;

import static com.planner.constants.AppConstants.*;

/**
 * @author mohit
 */
@Deprecated
@Repository
public class UserNeo4jRepository {

    @Inject
    private Session session;

    /**
     *
     * @param staffIds
     */

    public String getStaffByIdsAndSkillAndEmploymentAndExpertise(Long[] staffIds)
    {
        String cypherQuery="Match(staff:Staff)  where id(staff) in {staffIds} with staff " +
                           "Optional Match(skill:Skill{isEnabled:true})<-[:"+STAFF_HAS_SKILLS+"]-(staff) " +
                           "Optional Match(employment:Employment{history:false,published:true})<-[:"+BELONGS_TO_STAFF+"]-(staff) " +
                           "Optional Match(expertise:Expertise)<-[:"+HAS_EXPERTISE_IN+"]-(employment) " +
                           "return " +
                           "{" +
                           "staffId :id(staff),"+
                           "staffName:staff.firstName+staff.lastName,"+
                           "staffSkills:case when skill is null then [] else collect({skillid:id(skill),name:skill.name,weight:skill.weight}) end,"+
                          "staffEmployment:collect(employment) ," +
                           "employmentExpertise:expertise" +
                           "} as resultMap";
        Map<String,Object> map=new HashMap<>();
        map.put("staffIds",staffIds);
        Iterable<HashMap> result = session.query(HashMap.class,cypherQuery,map);
        List<HashMap> list=(List<HashMap>)result;
        return ObjectMapperUtils.objectToJsonString(list);
    }
}
