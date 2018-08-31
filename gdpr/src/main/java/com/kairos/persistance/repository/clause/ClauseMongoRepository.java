package com.kairos.persistance.repository.clause;

import com.kairos.persistance.model.clause.Clause;
import com.kairos.persistance.repository.custom_repository.MongoBaseRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigInteger;
import java.util.List;


@Repository
@JaversSpringDataAuditable
public interface ClauseMongoRepository extends MongoBaseRepository<Clause, BigInteger>, CustomClauseRepository {


    Clause findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0,organizationId:?1,_id:?2}")
    Clause findByIdAndNonDeleted(Long countryId, Long organizationId, BigInteger id);

    @Query("{deleted:false,countryId:?0,organizationId:?1,_id:{$in:?2}}")
    List<Clause> getClauseListByIds(Long countryId, Long organizationId, List<BigInteger> ClauseIds);


}
