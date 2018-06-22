package com.kairos.activity.persistence.repository.shift;

import com.kairos.activity.persistence.model.shift.ShiftTemplate;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.web.shift.ShiftTemplateDTO;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface ShiftTemplateMongoRepository extends MongoBaseRepository<ShiftTemplate,BigInteger> {
    List<ShiftTemplateDTO> findAllByUnitId(Long unitId);

    ShiftTemplate findByIdAndUnitIdAndDeletedFalse(BigInteger id,Long unitId);

    List<ShiftTemplateDTO> getAllShiftTemplatesByStaffId(Long unitId,Long staffId);

}
