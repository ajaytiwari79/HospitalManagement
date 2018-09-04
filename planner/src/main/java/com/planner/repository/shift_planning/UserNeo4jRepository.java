package com.planner.repository.shift_planning;

import org.bouncycastle.util.Arrays;
import org.neo4j.ogm.model.Property;

import org.neo4j.ogm.model.QueryStatistics;
import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.planner.constants.AppConstants.BELONGS_TO_STAFF;
import static com.planner.constants.AppConstants.HAS_EXPERTISE_IN;
import static com.planner.constants.AppConstants.STAFF_HAS_SKILLS;

/**
 * @author mohit
 */
@Repository
public class UserNeo4jRepository {

    @Inject
    private Session session;

    /**
     *
     * @param staffIds
     */

    public List<Map> getStaffByIdsAndSkillAndUnitPositionAndExpertise(Long[] staffIds)
    {
        String cypherQuery="Match(staff:Staff)  where id(staff) in {staffIds} with staff " +
                           "Optional Match(skill:Skill{isEnabled:true})<-[:"+STAFF_HAS_SKILLS+"]-(staff) " +
                           "Optional Match(unitPosition:UnitPosition{history:false,published:true})<-[:"+BELONGS_TO_STAFF+"]-(staff) " +
                           "Optional Match(expertise:Expertise)<-[:"+HAS_EXPERTISE_IN+"]-(unitPosition) " +
                           "return " +
                           "{" +
                           "staffId :id(staff),"+
                           "staffName:staff.firstName+staff.lastName,"+
                           "staffSkills:case when skill is null then [] else collect({skillid:id(skill),name:skill.name,weight:skill.weight}) end,"+
                          "staffUnitPosition:collect(unitPosition) ," +
                           "unitPositionExpertise:expertise" +
                           "} as resultMap";
        Map<String,Object> map=new HashMap<>();
        map.put("staffIds",staffIds);
        Iterable<Map> result = session.query(Map.class,cypherQuery,map);
        List<Map> list=(List<Map>)result;
        return  list;
    }
}
