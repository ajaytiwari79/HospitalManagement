package com.kairos.persistence.repository.user.country.default_data;


import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.country.default_data.account_type.AccountType;
import com.kairos.persistence.model.country.default_data.account_type.AccountTypeAccessGroupCountQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_ACCOUNT_TYPE;
import static com.kairos.persistence.model.constants.RelationshipConstants.IN_COUNTRY;

@Repository
public interface AccountTypeGraphRepository extends Neo4jBaseRepository<AccountType, Long> {

    @Query("match(country:Country)<-[:" + IN_COUNTRY + "]-(accountType:AccountType{deleted:false}) where id(country)={0} " +
            "RETURN accountType")
    List<AccountType> getAllAccountTypeByCountryId(Long countryId);

    @Query("match(country:Country)<-[:" + IN_COUNTRY + "]-(accountType:AccountType{deleted:false}) where id(country)={0} AND accountType.name =~{1} AND id(accountType)<>{2} " +
            " with count(accountType) as totalCount " +
            " RETURN CASE WHEN totalCount>0 THEN TRUE ELSE FALSE END as result")
    Boolean checkAccountTypeExistInCountry(Long countryId, String name, Long currentAccountTypeId);


    @Query("match(accountType:AccountType{deleted:false}) where id(accountType) IN {0} " +
            "RETURN accountType")
    List<AccountType> getAllAccountTypeByIds(Set<Long> accountTypeIds);

    @Query("match(country:Country)<-[:" + IN_COUNTRY + "]-(accountType:AccountType{deleted:false}) where id(country)={0} " +
            "Optional MATCH (ag:AccessGroup{deleted:false})-[:" + HAS_ACCOUNT_TYPE + "]->(accountType)" +
            "RETURN id(accountType) as id,accountType.name as name,count(ag) as count")
    List<AccountTypeAccessGroupCountQueryResult> getAllAccountTypeWithAccessGroupCountByCountryId(Long countryId);

    @Query("MATCH (accountType:AccountType{deleted:false}) where id(accountType)={0} " +
            "MATCH (ag:AccessGroup{deleted:false})-[:" + HAS_ACCOUNT_TYPE + "]->(accountType)" +
            "RETURN ag")
    List<AccessGroup> getAccessGroupsByAccountTypeId(Long accountTypeId);

}
