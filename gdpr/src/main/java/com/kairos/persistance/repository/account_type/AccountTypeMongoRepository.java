package com.kairos.persistance.repository.account_type;

import com.kairos.persistance.model.account_type.AccountType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
public interface AccountTypeMongoRepository extends  MongoRepository<AccountType,BigInteger> {

   @Query("{deleted:false,countryId:?0,organizationId:?1,_id:?2}")
   AccountType findByIdAndNonDeleted(Long countryId,Long organizationId,BigInteger id);

   @Query("{deleted:false,countryId:?0,organizationId:?1}")
   List<AccountType> getAllAccountType(Long countryId,Long organizationId);

   @Query("{deleted:false,countryId:?0,organizationId:?1,_id:{$in:?2}}")
   List<AccountType> getAccountTypeList(Long countryId,Long organizationId,Set<BigInteger> ids);

   AccountType findByid(BigInteger id);

   @Query("{deleted:false,countryId:?0,organizationId:?1,name:?2}")
  AccountType findByName(Long countryId,Long organizationId,String name);



}
