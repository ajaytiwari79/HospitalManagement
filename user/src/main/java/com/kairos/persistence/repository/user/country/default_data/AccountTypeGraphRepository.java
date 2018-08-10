package com.kairos.persistence.repository.user.country.default_data;


import com.kairos.persistence.model.country.default_data.account_type.AccountType;
import com.kairos.persistence.model.country.default_data.account_type.AccountTypeQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.IN_COUNTRY;

@Repository

public interface AccountTypeGraphRepository extends Neo4jBaseRepository<AccountType,Long> {

   //@Query("{deleted:false,countryId:?0,_id:?1}")
   AccountType findByIdAndNonDeleted(Long countryId,Long id);


   @Query("match(country:Country)<-[:"+IN_COUNTRY+"]-(accountType:AccountType{deleted:false}) where id(country)={0} " +
           "RETURN accountType" )
   List<AccountType> getAllAccountTypeByCountryId(Long countryId);

   //@Query("{deleted:false,countryId:?0,_id:{$in:?1}}")
   List<AccountType> getAccountTypeList(Set<Long> ids);

   AccountType findByid(BigInteger id);

   //@Query("{deleted:false,countryId:?0,name:?1}")
  AccountType findByName(Long countryId,String name);

   @Query("match(country:Country)<-[:"+IN_COUNTRY+"]-(accountType:AccountType{deleted:false}) where id(country)={0} AND accountType.name =~{1} AND id(accountType)<>{2} " +
           " with count(accountType) as totalCount " +
           " RETURN CASE WHEN totalCount>0 THEN TRUE ELSE FALSE END as result")
   Boolean checkAccountTypeExistInCountry(Long countryId,String name,Long currentAccountTypeId );
}
