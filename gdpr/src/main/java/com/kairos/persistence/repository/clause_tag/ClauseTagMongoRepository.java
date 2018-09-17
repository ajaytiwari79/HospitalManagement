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
public interface ClauseTagMongoRepository extends MongoBaseRepository<ClauseTag,BigInteger> {

    @Query("{countryId:?0,organizationId:?1,_id:?2,deleted:false}")
    ClauseTag findByIdAndNonDeleted(Long countryId,Long organizationId,BigInteger id);

    @Query("{deleted:false,countryId:?0,organizationId:?1}")
    List<ClauseTag> findAllClauseTag(Long countryId,Long organizationId);

    @Query("{countryId:?0,organizationId:?1,_id:{$in:?2},deleted:false}")
    List<ClauseTag> findAllClauseTagByIds(Long countryId,Long organizationId,List<BigInteger> ids);


    @Query("{deleted:false,countryId:?0,organizationId:?1,name:{$in:?2}}")
    List<ClauseTag> findTagByNames(Long countryId,Long organizationId,List<String> names);


    ClauseTag findByNameAndCountryId(Long countryId,Long organizationId,String name);
    ClauseTag findByid(BigInteger id);

}
