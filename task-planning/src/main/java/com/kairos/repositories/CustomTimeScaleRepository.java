package com.kairos.repositories;
import com.kairos.persistence.model.CustomTimeScale;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

/**
 * Created by oodles on 17/4/17.
 */
@Repository
public interface CustomTimeScaleRepository extends MongoRepository<CustomTimeScale,BigInteger> {

    CustomTimeScale findByStaffIdAndCitizenIdAndUnitId(Long staffId, Long citizenId, Long unitId);

}
