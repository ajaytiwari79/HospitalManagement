package com.kairos.persistence.repository.user.country;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;
import com.kairos.persistence.model.user.country.EmploymentType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;
import static com.kairos.persistence.model.constants.RelationshipConstants.EMPLOYMENT_TYPE_SETTINGS;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_EMPLOYMENT_TYPE;

/**
 * Created by prerna on 3/11/17.
 */
@Repository
public interface EmploymentTypeGraphRepository extends GraphRepository<EmploymentType>{

        List<EmploymentType> findAll();

        @Query("MATCH (n:Organization) - [r:"+BELONGS_TO+"] -> (c:Country)-[r1:"+HAS_EMPLOYMENT_TYPE+"]-> (et:EmploymentType)\n" +
                "WHERE id(n)={0} AND id(et)={1} AND et.deleted={2} return et")
        EmploymentType getEmploymentTypeByOrganization(Long organizationId, Long employmentTypeId, Boolean isDeleted);

        @Query("Match (o:Organization),(et:EmploymentType) where id(o) = {0} AND id(et) = {1}\n" +
                "MERGE (o)-[r:"+EMPLOYMENT_TYPE_SETTINGS+"]->(et)\n" +
                "ON CREATE SET r.allowedForContactPerson = {2}, r.allowedForShiftPlan = {3}, r.allowedForFlexPool = {4}, r.creationDate = {5},r.lastModificationDate = {4}\n" +
                "ON MATCH SET r.allowedForContactPerson = {2}, r.allowedForShiftPlan = {3}, r.allowedForFlexPool = {4}, r.lastModificationDate = {6} return true")
        Boolean setEmploymentTypeSettingsForOrganization(Long organizationId, Long employmentTypeId,
                                                         Boolean allowedForContactPerson,
                                                         boolean allowedForShiftPlan,
                                                         boolean allowedForFlexPool, long creationDate, long lastModificationDate);

        @Query("MATCH (o:Organization) - [r:BELONGS_TO] -> (c:Country)-[r1:HAS_EMPLOYMENT_TYPE]-> (et:EmploymentType) WHERE id(o)={0} AND et.deleted={1} with et\n" +
                "OPTIONAL MATCH (o)-[r:EMPLOYMENT_TYPE_SETTINGS]->(et) with \n" +
                "CASE WHEN r IS NULL THEN \n" +
                "collect({employmentType:{id:id(et),name:et.name,description:et.description},\n" +
                "allowedForContactPerson:o.allowedForContactPerson, allowedForShiftPlan:et.allowedForShiftPlan, allowedForFlexPool:et.allowedForFlexPool}) \n" +
                "ELSE\n" +
                "collect({employmentType:{id:id(et),name:et.name,description:et.description},\n" +
                "allowedForContactPerson:et.allowedForContactPerson, allowedForShiftPlan:et.allowedForShiftPlan, allowedForFlexPool:et.allowedForFlexPool})\n"+
                "END as employmentTypeSettings return employmentTypeSettings")
        List<HashMap<String, Object>> getEmploymentTypeSettingsForOrganization(long organizationId, boolean isDeleted);

        @Query("MATCH (n:Organization) - [r:"+BELONGS_TO+"] -> (c:Country)-[r1:"+HAS_EMPLOYMENT_TYPE+"]-> (et:EmploymentType)\n" +
                "WHERE id(n)={0} AND id(et)={1} AND et.deleted={2} return count(et) > 0 as etExists")
        Boolean isEmploymentTypeExistInOrganization(Long organizationId, Long employmentTypeId, Boolean isDeleted);

}
