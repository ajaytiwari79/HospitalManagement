package com.kairos.persistence.repository.user.staff;

import com.kairos.persistence.model.staff.permission.UnitPermission;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.staff.position.ExpiredPositionsQueryResult;
import com.kairos.persistence.model.staff.position.Position;
import com.kairos.persistence.model.staff.position.PositionReasonCodeQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
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

    @Query("MATCH (organization),(accessGroup:AccessGroup),(staff:Staff) WHERE id(organization)={1} AND id(accessGroup)={2} AND id(staff) ={0} WITH organization,accessGroup,staff\n" +
            "MATCH (staff)<-[:"+BELONGS_TO+"]-(position:Position)-[:"+ HAS_UNIT_PERMISSIONS +"]->(unitPermission:UnitPermission) WITH unitPermission,organization,accessGroup\n" +
            "MATCH (organization)<-[:"+ APPLICABLE_IN_UNIT +"]-(unitPermission)-[r:"+HAS_ACCESS_GROUP+"]->(accessGroup) WITH DISTINCT unitPermission,r,organization\n" +
            "RETURN {id:id(unitPermission),startDate:r.startDate,endDate:r.endDate,organizationId:id(organization),status:unitPermission.employmentStatus} AS data")
    Map<String,Object> getPositionOfParticularRole(long staffId, long organizationId, long accessGroupId);

    @Query("MATCH (organization:Organization),(staff:Staff),(unit) WHERE id(organization)={0} AND id(staff) IN {1} AND id(unit)={2}\n" +
            "create (organization)-[r:"+ HAS_POSITIONS +"]->(position:Position) WITH position,r,staff,organization,unit\n" +
            "create (position)-[r2:"+BELONGS_TO+"]->(staff) " +
            "create (position)-[:"+HAS_UNIT_PERMISSIONS+"]->(unitPermission:UnitPermission)-[:"+APPLICABLE_IN_UNIT+"]->(unit) RETURN r")
    void createPositions(long organizationId, List<Long> staffId, long unitId);


    @Query("MATCH (staff:Staff)<-[rel:"+ BELONGS_TO +"]-(position:Position) -[:HAS_UNIT_PERMISSIONS]->(unitPermission:UnitPermission) WHERE id(position) IN {0} \n" +
            "MATCH (unitPermission)-[:HAS_ACCESS_GROUP]->(accessGroup:AccessGroup{enabled:true}) \n" +
            "MATCH (unitPermission)-[:APPLICABLE_IN_UNIT]->(org:OrganizationBaseEntity) \n" +
            "WITH CASE WHEN org IS NULL THEN [] ELSE COLLECT(DISTINCT org) END AS units,position,rel,staff \n" +
            "RETURN position,rel,staff,units")
    List<ExpiredPositionsQueryResult> findExpiredPositionsAccessGroupsAndOrganizationsByEndDate(List<Long> positionIds);

    @Query("MATCH(staff:Staff)<-[:"+ BELONGS_TO +"]-(position:Position) WHERE id(staff) = {0} " +
            "OPTIONAL MATCH(position)-[:"+HAS_REASON_CODE+"]-(reasonCode:ReasonCode)  RETURN position, reasonCode")
    PositionReasonCodeQueryResult findEmploymentreasonCodeByStaff(Long staffId);

    @Query("MATCH(staff:Staff)<-[:"+ BELONGS_TO +"]-(position:Position) WHERE id(staff) = {0} " +
            "OPTIONAL MATCH(position)-[r:"+HAS_REASON_CODE+"]-(reasonCode:ReasonCode)" +
            "RETURN position,r,reasonCode")
    Position findByStaffId(Long staffId);


    @Query("MATCH(staff:Staff)<-[:"+ BELONGS_TO +"]-(position:Position) WHERE id(staff) = {0} set position.startDateMillis = {1}")
    void updatePositionStartDateOfStaff(Long staffId, Long startDateMillis);

    @Query("MATCH(staff:Staff)-[:" + BELONGS_TO + "]-(position:Position)" +
            " MATCH(position)-[r:"+ HAS_REASON_CODE +"]-(reasonCode:ReasonCode) WHERE id(staff)={0} delete r")
    void deletePositionReasonCodeRelation(Long staffId);
    
    @Query("MATCH(staff:Staff)-[:"+BELONGS_TO+"]-(position:Position) WHERE id(position)={0} RETURN staff")
    Staff findStaffByPositionId(Long positionId);

    @Query("MATCH (organization),(user:User) WHERE id(organization)={0} AND id(user)={1}\n" +
            "MATCH (user)-[:"+ BELONGS_TO +"]-(staff:Staff)" +
            "MATCH (organization)-[:"+ HAS_POSITIONS +"]->(position:Position{deleted:false})-[" + BELONGS_TO + "]->(staff) RETURN position")
    Position findPositionByOrganizationIdAndUserId(long organizationId, long userId);

    @Query("MATCH(position:Position) WHERE position.endDateMillis={0} \n" +
            "MATCH (staff)<-[:"+BELONGS_TO+"]-(position:Position)-[:"+HAS_UNIT_PERMISSIONS+"]->(unitPermission:UnitPermission) \n" +
            "MATCH (unitPermission)-[r:"+HAS_ACCESS_GROUP+"]->(accessGroup:AccessGroup) " +
            "RETURN DISTINCT id(position)")
    List<Long> findAllPositionsIdByEndDate(Long endDateMillis);

    @Query("MATCH(position:Position) where id(position) IN {0} \n" +
            "MATCH(position)-[:BELONGS_TO]->(staff:Staff)-[:BELONGS_TO]->(user:User) \n" +
            "RETURN DISTINCT user.userName")
    List<String> getAllUserByPositionIds(List<Long> positionIds);

    @Query("MATCH (p:Position)-[r:HAS_UNIT_PERMISSIONS]-(u:UnitPermission)-[:APPLICABLE_IN_UNIT]-(o:Organization) WHERE id(p)={0} AND id(o)={1}   RETURN Count(r)>0")
    boolean isunitPermissionExist(Long positionId,Long organizationId);

    @Query("MATCH (p:Position)-[r:HAS_UNIT_PERMISSIONS]-(u:UnitPermission)-[:HAS_ACCESS_GROUP]-(a:AccessGroup) WHERE id(p)={0} RETURN distinct id(a) as accessGroupId")
    Long findAccessGroupIdByPositionId(Long positionId);

    @Query("Match(a:AccessGroup) where id(a)={0}\n" +
            "Match(o:Organization) where id(o)={1}\n" +
            "Match(p:Position)-[r:HAS_UNIT_PERMISSIONS]-(u:UnitPermission)-[r1:HAS_ACCESS_GROUP]-(ap:AccessGroup) where id(p)={2} detach delete r1\n" +
            "CREATE (u)-[:HAS_ACCESS_GROUP]->(a)<-[:ORGANIZATION_HAS_ACCESS_GROUPS]-(o)")
    void updateAccessGroup(Long accessGroupId,Long organizationId,Long positionId);



}

