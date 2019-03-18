package com.kairos.persistence.repository.shift;

import com.kairos.persistence.model.shift.ShiftTemplate;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface ShiftTemplateRepository extends MongoBaseRepository<ShiftTemplate,BigInteger> {

    @Query("{'createdBy._id':?1,deleted:false,unitId:?0}")
    List<ShiftTemplate> findAllByUnitIdAndCreatedByAndDeletedFalse(Long unitId, Long createdBy);
    @Query("{deleted:false,id:?0}")
    ShiftTemplate findOneById(BigInteger id);

    @Query(value = "{'createdBy._id':?2,deleted:false,unitId:?1,name: {$regex : ?0, $options: 'i'}}",exists = true)
    boolean existsByNameIgnoreCaseAndDeletedFalseAndUnitId(String name,Long unitId,Long createdBy);
}
