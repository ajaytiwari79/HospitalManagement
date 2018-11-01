package com.kairos.persistence.repository.user.country.default_data;


import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.access_permission.AccessGroupQueryResult;
import com.kairos.persistence.model.country.default_data.account_type.AccountType;
import com.kairos.persistence.model.country.default_data.account_type.AccountTypeAccessGroupCountQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.DAY_TYPES;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_ACCOUNT_TYPE;
import static com.kairos.persistence.model.constants.RelationshipConstants.IN_COUNTRY;

@Repository
public interface AccountTypeGraphRepository extends Neo4jBaseRepository<AccountType, Long> {

    @Query("MATCH(country:Country)<-[:" + IN_COUNTRY + "]-(accountType:AccountType{deleted:false}) where id(country)={0} " +
            "RETURN accountType")
    List<AccountType> getAllAccountTypeByCountryId(Long countryId);

    @Query("MATCH(country:Country)<-[:" + IN_COUNTRY + "]-(accountType:AccountType{deleted:false}) where id(country)={0} AND accountType.name =~{1} AND id(accountType)<>{2} " +
            " WITH count(accountType) as totalCount " +
            " RETURN CASE WHEN totalCount>0 THEN TRUE ELSE FALSE END as result")
    Boolean checkAccountTypeExistInCountry(Long countryId, String name, Long currentAccountTypeId);


    @Query("MATCH(accountType:AccountType{deleted:false}) where id(accountType) IN {0} " +
            "RETURN accountType")
    List<AccountType> getAllAccountTypeByIds(Set<Long> accountTypeIds);

    @Query("MATCH(country:Country)<-[:" + IN_COUNTRY + "]-(accountType:AccountType{deleted:false}) where id(country)={0} " +
            "OPTIONAL MATCH (ag:AccessGroup{deleted:false})-[:" + HAS_ACCOUNT_TYPE + "]->(accountType)" +
            "RETURN id(accountType) as id,accountType.name as name,count(ag) as count")
    List<AccountTypeAccessGroupCountQueryResult> getAllAccountTypeWithAccessGroupCountByCountryId(Long countryId);

    @Query("MATCH (accountType:AccountType{deleted:false}) where id(accountType)={0} " +
            "MATCH (ag:AccessGroup{deleted:false})-[:" + HAS_ACCOUNT_TYPE + "]->(accountType) WHERE (ag.endDate IS NULL OR date(ag.endDate) >= date())" +
            "OPTIONAL MATCH(ag)-[:"+DAY_TYPES+"]->(dayType:DayType)" +
            "RETURN id(ag) as id, ag.name as name, ag.description as description, ag.typeOfTaskGiver as typeOfTaskGiver, ag.deleted as deleted, ag.role as role, ag.enabled as enabled,ag.startDate as startDate, ag.endDate as endDate, collect(dayType) as dayTypes,ag.allowedDayTypes as allowedDayTypes")
    List<AccessGroupQueryResult> getAccessGroupsByAccountTypeId(Long accountTypeId);

}
