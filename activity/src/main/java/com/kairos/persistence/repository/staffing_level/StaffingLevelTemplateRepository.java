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
    @Query("{ 'unitId' : ?0 ,disabled:false,deleted:false, 'validity.startDate' : { '$lte' : ?1},'validity.endDate' : { '$gte' : ?2}, 'dayType' :{ '$in' : ?3 }, 'validDays' : { '$in' : ?4} }")
    List<StaffingLevelTemplateDTO> findByUnitIdDayTypeAndDate(Long unitID, Date proposedStartDate, Date proposedEndDate, List<BigInteger> dayTypeId, List<String> days);

    @Query("{ 'unitId' : ?0 ,disabled:false,deleted:false, 'validity.startDate' : { '$lte' : ?1},'validity.endDate' : { '$gte' : ?2}, 'dayType' :{ '$in' : ?3 } }")
    List<StaffingLevelTemplateDTO> findByUnitIdHolidayDayTypeAndDate(Long unitID, Date proposedStartDate, Date proposedEndDate, List<BigInteger> dayTypeId);

    List<StaffingLevelTemplateDTO> findAllByUnitIdAndDeletedFalse(Long unitId);

    StaffingLevelTemplate findByIdAndUnitIdAndDeletedFalse(BigInteger staffingLevelTemplateId,Long unitId);

    boolean existsByNameIgnoreCaseAndDeletedFalseAndUnitId(String name,Long unitId);

}
