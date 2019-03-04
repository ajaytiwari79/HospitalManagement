package com.kairos.persistence.repository.user.staff;

import com.kairos.persistence.model.staff.employment.Position;
import com.kairos.persistence.model.staff.employment.EmploymentReasonCodeQueryResult;
import com.kairos.persistence.model.staff.employment.ExpiredEmploymentsQueryResult;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Created by prabjot on 3/12/16.
 */
@Repository
public interface PositionGraphRepository extends Neo4jBaseRepository<Position,Long> {

    @Query("MATCH (organization:Organization),(staff:Staff) WHERE id(organization)={0} AND id(staff)={1}\n" +
            "MATCH (organization)-[:"+ HAS_POSITIONS +"]->(position:Position)-[BELONGS_TO]->(staff) RETURN position")
    Position findPosition(long organizationId, long staffId);

    @Query("MATCH (organization:Organization),(accessGroup:AccessGroup),(staff:Staff) WHERE id(organization)={1} AND id(accessGroup)={2} AND id(staff) ={0} WITH organization,accessGroup,staff\n" +
            "MATCH (staff)<-[:"+BELONGS_TO+"]-(position:Position)-[:"+ HAS_UNIT_PERMISSIONS +"]->(unitPermission:UnitPermission) WITH unitPermission,organization,accessGroup\n" +
            "MATCH (organization)<-[:"+ APPLICABLE_IN_UNIT +"]-(unitPermission)-[:HAS_ACCESS_GROUP]->(accessGroup) WITH DISTINCT unitPermission,organization\n" +
            "RETURN {id:id(unitPermission),startDate:unitPermission.startDate,endDate:unitPermission.endDate,organizationId:id(organization),status:unitPermission.employmentStatus} AS data")
    Map<String,Object> getPositionOfParticularRole(long staffId, long organizationId, long accessGroupId);

    @Query("MATCH (organization:Organization),(staff:Staff),(unit:Organization) WHERE id(organization)={0} AND id(staff) IN {1} AND id(unit)={2}\n" +
            "create (organization)-[r:"+ HAS_POSITIONS +"]->(position:Position) WITH position,r,staff,organization,unit\n" +
            "create (position)-[r2:"+BELONGS_TO+"]->(staff) " +
            "create (position)-[:"+HAS_UNIT_PERMISSIONS+"]->(unitPermission:UnitPermission)-[:"+APPLICABLE_IN_UNIT+"]->(unit) RETURN r")
    void createPositions(long organizationId, List<Long> staffId, long unitId);


    @Query("MATCH(position:Position)-[r1:" + BELONGS_TO + "]->(staff:Staff) WHERE id(position) in {0} \n" +
            "MATCH(position)-[:" + HAS_UNIT_PERMISSIONS + "]->(unitPermission:UnitPermission)-[:" + APPLICABLE_IN_UNIT + "]->(org:Organization) RETURN position, \n" +
            "CASE WHEN org IS NOT NULL THEN COLLECT( DISTINCT org) else[] end AS organizations, \n" +
            "CASE WHEN unitPermission is NOT null THEN COLLECT(DISTINCT unitPermission) else[] end AS unitPermissions")
    List<ExpiredEmploymentsQueryResult> findExpiredPositionsAccessGroupsAndOrganizationsByEndDate(List<Long> positionIds);

    @Query("MATCH(staff:Staff)<-[:"+ BELONGS_TO +"]-(position:Position) WHERE id(staff) = {0} " +
            "OPTIONAL MATCH(position)-[:"+HAS_REASON_CODE+"]-(reasonCode:ReasonCode)  RETURN position, reasonCode")
    EmploymentReasonCodeQueryResult findEmploymentreasonCodeByStaff(Long staffId);

    @Query("MATCH(staff:Staff)<-[:"+ BELONGS_TO +"]-(position:Position) WHERE id(staff) = {0} RETURN position")
    Position findByStaffId(Long staffId);


    @Query("MATCH(staff:Staff)<-[:"+ BELONGS_TO +"]-(position:Position) WHERE id(staff) = {0} set position.startDateMillis = {1}")
    void updatePositionStartDateOfStaff(Long staffId, Long endDateMillis);

    @Query("MATCH(staff:Staff)-[:" + BELONGS_TO + "]-(position:Position)" +
            " MATCH(position)-[r:"+ HAS_REASON_CODE +"]-(reasonCode:ReasonCode) WHERE id(staff)={0} delete r")
    void deletePositionReasonCodeRelation(Long staffId);
    
    @Query("MATCH(staff:Staff)-[:"+BELONGS_TO+"]-(position:Position) WHERE id(position)={0} RETURN id(staff)")
    Long findStaffByPositionId(Long positionId);


}

