package com.kairos.persistance.repository.clause;

import com.kairos.persistance.model.clause.Clause;
import com.kairos.response.dto.clause.ClauseResponseDTO;
import org.bson.types.ObjectId;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;


@Repository
@JaversSpringDataAuditable
public interface ClauseMongoRepository extends MongoRepository<Clause,ObjectId>,CustomClauseRepository{


    @Query("{deleted:false,countryId:?0,organizationId:?1,title:?2}")
    Clause findByTitleAndCountry(Long countryId,Long organizationId,String title);

    Clause findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0,organizationId:?1,_id:?2}")
    Clause findByIdAndNonDeleted(Long countryId,Long organizationId,ObjectId id);

    @Query("{deleted:false,countryId:?0,organizationId:?1}")
    List<ClauseResponseDTO>  findAllClause(Long countryId,Long organizationId);

    @Query("{deleted:false,countryId:?0,organizationId:?1,_id:{$in:?2}}")
    List<Clause>  getClauseListByIds(Long countryId,Long organizationId, Set<ObjectId> ids);



}
