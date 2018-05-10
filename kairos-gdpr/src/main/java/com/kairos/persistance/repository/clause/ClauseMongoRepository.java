package com.kairos.persistance.repository.clause;

import com.kairos.persistance.model.clause.Clause;
import com.kairos.persistance.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;


@Repository
public interface ClauseMongoRepository extends MongoRepository<Clause,BigInteger>{


    Clause findByTitle(String title);

    Clause findByid(BigInteger id);

    @Query("{'accountType':?0}")
    List<Clause> getClauseByAccountType(String accountType);


    @Query("{$and:[{'organizationType.name':?0},{'organizationService.name':?1},{'accountType.accountTypeEnum':?2}]}")
    List<Clause> getClauseByOrgnizationServiceAndTypeAndAccount(String orgType,String orgService,String accountType);



}
