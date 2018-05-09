package com.kairos.persistance.repository.clause;

import com.kairos.persistance.model.clause.AccountType;
import com.kairos.persistance.model.clause.Clause;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.math.BigInteger;

@Repository
public interface AccountTypeMongoRepository extends  MongoRepository<AccountType,BigInteger> {


   AccountType findByid(BigInteger id);

   @Query("{'typeOfAccount':?0}")
   AccountType findByTypeOfAccount(String typeOfAccount);
}
