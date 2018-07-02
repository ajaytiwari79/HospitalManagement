package com.kairos.persistance.repository.clause_tag;

import com.kairos.persistance.model.clause_tag.ClauseTag;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface ClauseTagMongoRepository extends MongoRepository<ClauseTag,ObjectId> {

    @Query("{countryId:?0,organizationId:?1,_id:?2,deleted:false}")
    ClauseTag findByIdAndNonDeleted(Long countryId,Long organizationId,ObjectId id);

    @Query("{deleted:false,countryId:?0,organizationId:?1}")
    List<ClauseTag> findAllClauseTag(Long countryId,Long organizationId);

    @Query("{countryId:?0,organizationId:?1,_id:{$in:?2},deleted:false}")
    List<ClauseTag> findAllClauseTagByIds(Long countryId,Long organizationId,List<ObjectId> ids);


    @Query("{deleted:false,countryId:?0,organizationId:?1,name:{$in:?2}}")
    List<ClauseTag> findTagByNames(Long countryId,Long organizationId,List<String> names);


    ClauseTag findByNameAndCountryId(Long countryId,Long organizationId,String name);
    ClauseTag findByid(ObjectId id);

}
