package com.planner.repository.shift_planning;

import com.planner.domain.query_results.dummy_model.Dummy;
import com.planner.domain.query_results.StaffQueryResult;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

//@Repository
public interface UserNeo4jRepo extends Neo4jRepository<Dummy,Long>{

    @Query("Match(staff:Staff)  where id(staff) in {0} with staff " +
            //"Optional Match(skill:Skill{isEnabled:true})<-[:"+STAFF_HAS_SKILLS+"]-(staff) " +
            //"Optional Match(unitPosition:UnitPosition{history:false,published:true})<-[:"+BELONGS_TO_STAFF+"]-(staff) " +
           // "Optional Match(expertise:Expertise)<-[:"+HAS_EXPERTISE_IN+"]-(unitPosition) " +
            "return " +
            "id(staff) as staffId,"+
            "staff.firstName+staff.lastName as staffName"//+
           // "staffSkills:case when skill is null then [] else collect({skillid:id(skill),name:skill.name,weight:skill.weight}) end,"+
            //"staffUnitPosition:collect(unitPosition) ," +
           // "unitPositionExpertise:expertise"
            )
    StaffQueryResult getStaffData(Long[] staffIds);
}
