package com.kairos.persistence.repository.shift;

import com.kairos.dto.activity.shift.CoverShiftDTO;
import com.kairos.persistence.model.shift.CoverShift;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

public interface CoverShiftMongoRepository extends MongoBaseRepository<CoverShift, BigInteger> {

    CoverShift findByShiftIdAndStaffIdAndDeletedFalse(BigInteger shiftId, Long staffId);

    CoverShift findByIdAndDeletedFalse(BigInteger id);

    @Query("{deleted:false,date:{$gte:?0,$lte:?1}}")
    List<CoverShiftDTO> findAllByDateGreaterThanEqualsAndLessThanEqualsAndDeletedFalse(LocalDate startDate, LocalDate endDate);
}
