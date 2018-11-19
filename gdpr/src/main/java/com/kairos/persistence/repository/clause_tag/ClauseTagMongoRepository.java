package com.kairos.persistence.repository.clause_tag;

import com.kairos.persistence.model.clause_tag.ClauseTag;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface ClauseTagMongoRepository extends MongoBaseRepository<ClauseTag,BigInteger>,CustomClauseTagRepository {

    @Query("{countryId:?0,_id:?1,deleted:false}")
    ClauseTag findByIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{deleted:false,countryId:?0}")
    List<ClauseTag> findAllByCountryId(Long countryId);

    @Query("{deleted:false,organizationId:?0}")
    List<ClauseTag> findAllClauseTagByUnitId(Long unitId);

    @Query("{_id:{$in:?0},deleted:false}")
    List<ClauseTag> findAllClauseTagByIds(List<BigInteger> ids);


    @Query("{deleted:false,countryId:?0,defaultTag:true}")
    ClauseTag findDefaultTagByCountryId(Long countryId);

    @Query("{deleted:false,organizationId:?0,defaultTag:true}")
    ClauseTag findDefaultTagByUnitId(Long unitId);

    ClauseTag findByNameAndCountryId(Long countryId,String name);
    ClauseTag findByid(BigInteger id);

}
