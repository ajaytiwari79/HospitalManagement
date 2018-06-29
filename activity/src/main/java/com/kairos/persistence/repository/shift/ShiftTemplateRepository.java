package com.kairos.persistence.repository.shift;

import com.kairos.persistence.model.shift.ShiftTemplate;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface ShiftTemplateRepository extends MongoBaseRepository<ShiftTemplate,BigInteger> {
    List<ShiftTemplate> findAllByUnitIdAndCreatedByAndDeletedFalse(Long unitId, Long userId);
    @Query("{deleted:false,id:?0}")
    ShiftTemplate findOneById(BigInteger id);
}
