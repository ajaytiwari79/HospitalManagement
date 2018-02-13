package com.kairos.persistence.repository.user.country;
import com.kairos.persistence.model.user.country.dto.EmploymentTypeDTO;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
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
public interface EmploymentTypeGraphRepository extends Neo4jBaseRepository<EmploymentType,Long>{

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
                "o,et,r \n" +
                "return id(et) as id, et.name as name, et.description as description,\n" +
                "CASE WHEN r IS null THEN et.allowedForContactPerson ELSE r.allowedForContactPerson  END AS allowedForContactPerson,\n" +
                "CASE WHEN r IS null THEN et.allowedForShiftPlan ELSE r.allowedForShiftPlan  END AS allowedForShiftPlan,\n" +
                "CASE WHEN r IS null THEN et.allowedForFlexPool ELSE r.allowedForFlexPool  END AS allowedForFlexPool")
        List<EmploymentTypeDTO> getEmploymentTypeSettingsForOrganization(long organizationId, boolean isDeleted);

        @Query("MATCH (n:Organization) - [r:"+BELONGS_TO+"] -> (c:Country)-[r1:"+HAS_EMPLOYMENT_TYPE+"]-> (et:EmploymentType)\n" +
                "WHERE id(n)={0} AND id(et)={1} AND et.deleted={2} return count(et) > 0 as etExists")
        Boolean isEmploymentTypeExistInOrganization(Long organizationId, Long employmentTypeId, Boolean isDeleted);

        @Query("MATCH (et:EmploymentType) WHERE id(et) IN {0} AND et.deleted={1} return et")
        List<EmploymentType> getEmploymentTypeByIds(List<Long> employmentTypeIds, Boolean isDeleted);

        @Query("MATCH (country:Country)-[:"+HAS_EMPLOYMENT_TYPE+"]->(employmentType:EmploymentType) where id(country)={0} AND employmentType.deleted={1} return employmentType LIMIT 1")
        EmploymentType getOneEmploymentTypeByCountryId(Long countryId, Boolean isDeleted);




}
