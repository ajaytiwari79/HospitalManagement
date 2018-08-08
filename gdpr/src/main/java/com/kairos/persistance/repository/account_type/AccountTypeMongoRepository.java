package com.kairos.persistance.repository.account_type;

import com.kairos.persistance.model.account_type.AccountType;
import com.kairos.persistance.repository.custom_repository.MongoBaseRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
public interface AccountTypeMongoRepository extends MongoBaseRepository<AccountType,BigInteger> {

   @Query("{deleted:false,countryId:?0,_id:?1}")
   AccountType findByIdAndNonDeleted(Long countryId,BigInteger id);

   @Query("{deleted:false,countryId:?0}")
   List<AccountType> getAllAccountType(Long countryId);

   @Query("{deleted:false,countryId:?0,_id:{$in:?1}}")
   List<AccountType> getAccountTypeList(Long countryId,Set<BigInteger> ids);

   AccountType findByid(BigInteger id);

   @Query("{deleted:false,countryId:?0,name:?1}")
  AccountType findByName(Long countryId,String name);


}
