package com.kairos.persistance.repository.clause_tag;

import com.kairos.persistance.model.clause_tag.ClauseTag;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface ClauseTagMongoRepository extends MongoRepository<ClauseTag,BigInteger> {

    @Query("{countryId:?0,_id:?1,deleted:false}")
    ClauseTag findByIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{deleted:false,countryId:?0}")
    List<ClauseTag> findAllClauseTag(Long countryId);

    @Query("{countryId:?0,_id:{$in:?1},deleted:false}")
    List<ClauseTag> findAllClauseTagByIds(Long countryId,List<BigInteger> ids);


    @Query("{deleted:false,countryId:?0,name:{$in:?1}}")
    List<ClauseTag> findTagByNames(Long countryId,List<String> names);


    ClauseTag findByNameAndCountryId(Long countryId,String name);
    ClauseTag findByid(BigInteger id);

}
