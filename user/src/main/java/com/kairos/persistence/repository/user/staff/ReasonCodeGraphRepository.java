package com.kairos.persistence.repository.user.staff;

import com.kairos.enums.SkillLevel;
import com.kairos.persistence.model.auth.ReasonCode;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.organization.StaffTeamRelationship;
import com.kairos.persistence.model.organization.Unit;
import com.kairos.persistence.model.staff.*;
import com.kairos.persistence.model.staff.permission.UnitStaffQueryResult;
import com.kairos.persistence.model.staff.personal_details.*;
import com.kairos.persistence.model.staff.position.StaffPositionDTO;
import com.kairos.persistence.model.user.employment.query_result.StaffEmploymentDetails;
import com.kairos.persistence.model.user.expertise.response.ExpertiseLocationStaffQueryResult;
import com.kairos.persistence.model.user.filter.FavoriteFilterQueryResult;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.*;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Created by prabjot on 24/10/16.
 */
@Repository
public interface ReasonCodeGraphRepository extends Neo4jBaseRepository<ReasonCode, Long> {

}

