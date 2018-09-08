package com.kairos.persistence.repository.shift;

import com.kairos.persistence.model.shift.IndividualShiftTemplate;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
public interface IndividualShiftTemplateRepository extends MongoBaseRepository<IndividualShiftTemplate,BigInteger> ,CustomIndividualShiftTemplateRepository {

      List<IndividualShiftTemplate> getAllByIdInAndDeletedFalse(Set<BigInteger> individualShiftTemplateIds);

      @Query("{deleted:false,id:?0}")
      IndividualShiftTemplate findOneById(BigInteger id);

}
