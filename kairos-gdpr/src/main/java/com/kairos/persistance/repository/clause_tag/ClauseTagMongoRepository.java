package com.kairos.persistance.repository.clause_tag;

import com.kairos.persistance.model.clause_tag.ClauseTag;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface ClauseTagMongoRepository extends MongoRepository<ClauseTag,BigInteger> {

    @Query("{'_id':?0,deleted:false}")
    ClauseTag findByIdAndNonDeleted(BigInteger id);

    @Query("{deleted:false}")
    List<ClauseTag> findAllClauseTag();

    ClauseTag findByName(String name);

}
