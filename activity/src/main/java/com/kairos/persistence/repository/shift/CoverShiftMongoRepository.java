package com.kairos.persistence.repository.shift;

import com.kairos.persistence.model.shift.CoverShift;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

public interface CoverShiftMongoRepository extends MongoBaseRepository<CoverShift, BigInteger> {

    CoverShift findByShiftIdAndStaffIdAndDeletedFalse(BigInteger shiftId, Long staffId);

    CoverShift findByIdAndDeletedFalse(BigInteger id);

    List<CoverShift> findAllByDateGreaterThanEqualsAndLessThanEqualsAndDeletedFalse(LocalDate startDate, LocalDate endDate);
}
