package com.kairos.persistence.repository.shift;

import com.kairos.persistence.model.shift.ShiftTemplate;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface ShiftTemplateRepository extends MongoBaseRepository<ShiftTemplate,BigInteger> {

    @Query("{'deleted':false,'unitId':?0,'createdBy._id':?1}")
    List<ShiftTemplate> findAllByUnitIdAndCreatedByAndDeletedFalse(Long unitId, Long createdBy);
    @Query("{deleted:false,id:?0}")
    ShiftTemplate findOneById(BigInteger id);

    boolean existsByNameIgnoreCaseAndDeletedFalseAndUnitId(String name,Long unitId);
}
