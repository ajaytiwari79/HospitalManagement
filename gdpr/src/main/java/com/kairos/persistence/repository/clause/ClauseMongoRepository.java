package com.kairos.persistence.repository.clause;

import com.kairos.persistence.model.clause.Clause;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigInteger;
import java.util.List;


@Repository
@JaversSpringDataAuditable
public interface ClauseMongoRepository extends MongoBaseRepository<Clause, BigInteger>, CustomClauseRepository {


    Clause findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0,_id:?1}")
    Clause findByIdAndNonDeleted(Long countryId, BigInteger id);

    @Query("{deleted:false,countryId:?0,_id:{$in:?1}}")
    List<Clause> findClauseByCountryIdAndIdList(Long countryId, List<BigInteger> ClauseIds);


}
