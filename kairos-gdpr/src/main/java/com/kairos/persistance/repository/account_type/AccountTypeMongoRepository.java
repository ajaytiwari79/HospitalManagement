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

   AccountType findByid(BigInteger id);

   @Query("{deleted:false}")
   List<AccountType> getAllAccountType();

   @Query("{deleted:false,'_id':{$in:?0}}")
   List<AccountType> getAccountTypeList(Set<BigInteger> ids);

   @Query("{'typeOfAccount':?0}")
   AccountType findByTypeOfAccount(String typeOfAccount);

}
