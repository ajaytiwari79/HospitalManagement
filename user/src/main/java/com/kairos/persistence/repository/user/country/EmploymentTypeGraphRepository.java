package com.kairos.persistence.repository.user.country;

import com.kairos.enums.shift.PaidOutFrequencyEnum;
import com.kairos.persistence.model.country.default_data.EmploymentTypeDTO;
import com.kairos.persistence.model.country.employment_type.EmploymentType;
import com.kairos.persistence.model.country.employment_type.EmploymentTypeQueryResult;
import com.kairos.persistence.model.user.filter.FilterSelectionQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by prerna on 3/11/17.
 */
@Repository
public interface EmploymentTypeGraphRepository extends Neo4jBaseRepository<EmploymentType, Long> {

    List<EmploymentType> findAll();

    @Query("MATCH (n:Organization) - [r:" + BELONGS_TO + "] -> (c:Country)-[r1:" + HAS_EMPLOYMENT_TYPE + "]-> (et:EmploymentType)\n" +
            "WHERE id(n)={0} AND id(et)={1} AND et.deleted={2} return et")
    EmploymentType getEmploymentTypeByOrganization(Long organizationId, Long employmentTypeId, Boolean isDeleted);

    @Query("Match (o:Organization),(et:EmploymentType) where id(o) = {0} AND id(et) = {1}\n" +
            "MERGE (o)-[r:" + EMPLOYMENT_TYPE_SETTINGS + "]->(et)\n" +
            "ON CREATE SET r.allowedForContactPerson = {2}, r.allowedForShiftPlan = {3}, r.allowedForFlexPool = {4}, r.paymentFrequency = {5}, r.creationDate = {6},r.lastModificationDate = {7}\n" +
            "ON MATCH SET r.allowedForContactPerson = {2}, r.allowedForShiftPlan = {3}, r.allowedForFlexPool = {4}, r.paymentFrequency = {5}, r.lastModificationDate = {7} return true")
    Boolean setEmploymentTypeSettingsForOrganization(Long organizationId, Long employmentTypeId,
                                                     Boolean allowedForContactPerson,
                                                     boolean allowedForShiftPlan,
                                                     boolean allowedForFlexPool, PaidOutFrequencyEnum paymentFrequency, long creationDate, long lastModificationDate);

    @Query("MATCH  (c:Country)-[r1:HAS_EMPLOYMENT_TYPE]-> (et:EmploymentType) WHERE  id(c)={0} AND et.deleted={2}  with et\n" +
            "MATCH (o:Organization)-[r:EMPLOYMENT_TYPE_SETTINGS]->(et) WHERE id(o)={1}  WITH\n" +
            "o,et,r \n" +
            "return id(et) as id, et.name as name, et.description as description,et.employmentCategories as employmentCategories, \n" +
            "CASE WHEN r IS null THEN et.paymentFrequency ELSE r.paymentFrequency END as paymentFrequency, \n" +
            "CASE WHEN r IS null THEN et.allowedForContactPerson ELSE r.allowedForContactPerson  END AS allowedForContactPerson,\n" +
            "CASE WHEN r IS null THEN et.allowedForShiftPlan ELSE r.allowedForShiftPlan  END AS allowedForShiftPlan,\n" +
            "CASE WHEN r IS null THEN et.allowedForFlexPool ELSE r.allowedForFlexPool  END AS allowedForFlexPool")
    List<EmploymentTypeDTO> getCustomizedEmploymentTypeSettingsForOrganization(long countryId, long organizationId, boolean isDeleted);

    @Query("MATCH  (c:Country)-[r1:HAS_EMPLOYMENT_TYPE]-> (et:EmploymentType) WHERE  id(c)={0} AND  NOT (ID(et) IN {3}) AND et.deleted={2}  with et\n" +
            "OPTIONAL MATCH (o:Organization)-[r:EMPLOYMENT_TYPE_SETTINGS]->(et) WHERE id(o)={1}  WITH\n" +
            "o,et,r \n" +
            "return id(et) as id, et.name as name, et.description as description,et.employmentCategories as employmentCategories , et.paymentFrequency as paymentFrequency, \n" +
            "CASE WHEN r IS null THEN et.allowedForContactPerson ELSE r.allowedForContactPerson  END AS allowedForContactPerson,\n" +
            "CASE WHEN r IS null THEN et.allowedForShiftPlan ELSE r.allowedForShiftPlan  END AS allowedForShiftPlan,\n" +
            "CASE WHEN r IS null THEN et.allowedForFlexPool ELSE r.allowedForFlexPool  END AS allowedForFlexPool")
    List<EmploymentTypeDTO> getEmploymentTypeSettingsForOrganization(long countryId, long organizationId, boolean isDeleted, List<Long> excludeEmploymentTypeIds);

    @Query("MATCH (n:Organization) - [r:" + BELONGS_TO + "] -> (c:Country)-[r1:" + HAS_EMPLOYMENT_TYPE + "]-> (et:EmploymentType)\n" +
            "WHERE id(n)={0} AND id(et)={1} AND et.deleted={2} return count(et) > 0 as etExists")
    Boolean isEmploymentTypeExistInOrganization(Long organizationId, Long employmentTypeId, Boolean isDeleted);

    @Query("MATCH (et:EmploymentType) WHERE id(et) IN {0} AND et.deleted={1} return et")
    List<EmploymentType> getEmploymentTypeByIds(List<Long> employmentTypeIds, Boolean isDeleted);

    @Query("MATCH (et:EmploymentType{deleted:false}) WHERE id(et) IN {0}  return et")
    List<EmploymentType> getEmploymentTypeByIds(Set<Long> employmentTypeIds);

    @Query("MATCH (n:Organization) - [:" + BELONGS_TO + "] -> (c:Country)-[:" + HAS_EMPLOYMENT_TYPE + "]-> (et:EmploymentType)\n" +
            "WHERE id(n)={0} AND et.deleted={1} return et")
    List<EmploymentType> getAllEmploymentTypeByOrganization(Long organizationId, Boolean isDeleted);

    @Query("MATCH (country:Country)-[:" + HAS_EMPLOYMENT_TYPE + "]->(employmentType:EmploymentType) where id(country)={0} AND employmentType.deleted={1} return employmentType LIMIT 1")
    EmploymentType getOneEmploymentTypeByCountryId(Long countryId, Boolean isDeleted);

    @Query("MATCH (country:Country)-[:" + HAS_EMPLOYMENT_TYPE + "]->(employmentType:EmploymentType{deleted:false}) where id(country)={0} AND employmentType.name=~{1} AND id(employmentType) <> {2} " +
            "with count(employmentType) as employmentTypeCount return CASE when employmentTypeCount>0 THEN  true ELSE false END as response")
    boolean findByNameExcludingCurrent(Long countryId, String name, Long employmentTypeId);


    // Get Employment Type data for filters by countryId
    @Query("MATCH (country:Country)-[:" + HAS_EMPLOYMENT_TYPE + "]->(employmentType:EmploymentType{deleted:false}) where id(country)={0} return toString(id(employmentType)) as id, employmentType.name as value")
    List<FilterSelectionQueryResult> getEmploymentTypeByCountryIdForFilters(Long countryId);

    @Query("MATCH  (c:Country)-[:" + HAS_EMPLOYMENT_TYPE + "]-> (et:EmploymentType)\n" +
            "WHERE id(c)={0} AND et.deleted={1} return id(et) as id,et.name as name")
    List<EmploymentTypeQueryResult> getEmploymentTypeByCountry(Long countryId, Boolean isDeleted);


}
