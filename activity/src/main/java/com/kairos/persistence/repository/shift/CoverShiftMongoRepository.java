package com.kairos.persistence.repository.shift;

import com.kairos.persistence.model.shift.CoverShift;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;

import java.math.BigInteger;

public interface CoverShiftMongoRepository extends MongoBaseRepository<CoverShift, BigInteger> {

    CoverShift findByShiftIdAndStaffIdDeletedFalse(BigInteger shiftId, Long staffId);

    CoverShift findByIdAndDeletedFalse(BigInteger id);
}
