package com.kairos.activity.persistence.repository.shift;

import com.kairos.activity.persistence.model.shift.IndividualShiftTemplate;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.web.shift.IndividualShiftTemplateDTO;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
public interface IndividualShiftTemplateMongoRepository extends MongoBaseRepository<IndividualShiftTemplate,BigInteger> {
//    List<IndividualShiftTemplateDTO> findAllByUnitId(Long unitId);
//
//    IndividualShiftTemplate findByIdAndUnitIdAndDeletedFalse(BigInteger id, Long unitId);
//
//    List<IndividualShiftTemplateDTO> getAllShiftTemplatesByStaffId(Long unitId, Long staffId);

      List<IndividualShiftTemplateDTO> findAllByIdInAndDeletedFalse(Set<BigInteger> shiftDayTemplateIds);

      List<IndividualShiftTemplate> getAllByIdInAndDeletedFalse(Set<BigInteger> shiftDayTemplateIds);

      @Query("{deleted:false,id:?0}")
      IndividualShiftTemplate findOneById(BigInteger id);

}
