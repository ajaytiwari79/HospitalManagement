package com.planner.repository.shift_planning;

import com.planner.domain.query_results.dummy_model.Dummy;
import com.planner.domain.query_results.StaffQueryResult;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

import static com.planner.constants.AppConstants.*;

//@Repository
public interface UserNeo4jRepo extends Neo4jRepository<Dummy, Long> {

    @Query( "Match(unit:Organization) where id(unit)={0} "+
            "Match(staff:Staff)  where id(staff) in {1} with staff,unit " +
            "Optional Match(skill:Skill{isEnabled:true})<-[:" + STAFF_HAS_SKILLS + "]-(staff) " +
            "Optional Match(unit)<-[:"+IN_UNIT+"]-(unitPosition:UnitPosition{history:false,published:true})<-[:" + BELONGS_TO_STAFF + "]-(staff) " +
            //"Optional Match(expertise:Expertise)<-[:" + HAS_EXPERTISE_IN + "]-(unitPosition) " +
            "return " +
            "id(staff) as staffId," +
            "staff.firstName+staff.lastName as staffName," +
            "collect({skillId:id(skill),name:skill.name,weight:skill.weight}) as staffSkills,"+
            "id(unitPosition) as unitPositionsId "
            // "unitPositionExpertise:expertise"
    )
    List<StaffQueryResult> getStaffWithSkillsAndUnitPostionIds(Long unitId,Long[] staffIds);
}
