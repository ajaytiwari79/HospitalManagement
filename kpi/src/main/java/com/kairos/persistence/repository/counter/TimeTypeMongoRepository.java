package com.kairos.persistence.repository.counter;

import com.kairos.dto.activity.time_type.TimeTypeDTO;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
public class TimeTypeMongoRepository {
    public Set<BigInteger> findTimeTypeIdssByTimeTypeEnum(List list) {
        return null;
    }
    public Set<BigInteger> findAllTimeTypeIdsByTimeTypeIds(List bigIntegerValue) {
        return null;
    }
    public List<TimeTypeDTO> getAllTimeTypesByCountryId(Long countryId) {
        return null;
    }
}
