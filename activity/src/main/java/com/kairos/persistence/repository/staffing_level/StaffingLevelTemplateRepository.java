package com.kairos.persistence.repository.staffing_level;

import com.kairos.dto.activity.staffing_level.StaffingLevelTemplateDTO;
import com.kairos.persistence.model.staffing_level.StaffingLevelTemplate;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Repository
public interface StaffingLevelTemplateRepository extends MongoBaseRepository<StaffingLevelTemplate,BigInteger>,CustomStaffingLevelTemplateRepository {

    List<StaffingLevelTemplateDTO> findAllByUnitIdAndDeletedFalse(Long unitId);

    StaffingLevelTemplate findByIdAndUnitIdAndDeletedFalse(BigInteger staffingLevelTemplateId,Long unitId);

    boolean existsByNameIgnoreCaseAndDeletedFalseAndUnitId(String name,Long unitId);

}
