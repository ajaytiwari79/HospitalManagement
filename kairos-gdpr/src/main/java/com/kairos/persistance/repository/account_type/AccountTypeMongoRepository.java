package com.kairos.persistance.repository.account_type;

import com.kairos.persistance.model.account_type.AccountType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface AccountTypeMongoRepository extends  MongoRepository<AccountType,BigInteger> {


   AccountType findByid(BigInteger id);

   @Query("{deleted:false,countryId:4}")
   List<AccountType> getAllAccountType(long  countryId);

   @Query("{'typeOfAccount':?0}")
   AccountType findByTypeOfAccount(String typeOfAccount);

}
