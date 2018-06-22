package com.kairos.activity.persistence.repository.shift;

import com.kairos.activity.persistence.model.shift.ShiftDayTemplate;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.web.shift.ShiftDayTemplateDTO;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface ShiftDayTemplateMongoRepository extends MongoBaseRepository<ShiftDayTemplate,BigInteger> {
    List<ShiftDayTemplateDTO> findAllByUnitId(Long unitId);

    ShiftDayTemplate findByIdAndUnitIdAndDeletedFalse(BigInteger id, Long unitId);

    List<ShiftDayTemplateDTO> getAllShiftTemplatesByStaffId(Long unitId, Long staffId);

}
