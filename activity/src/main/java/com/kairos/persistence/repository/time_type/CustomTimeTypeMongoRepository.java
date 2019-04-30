package com.kairos.persistence.repository.time_type;

import com.kairos.dto.activity.time_type.TimeTypeDTO;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

public interface CustomTimeTypeMongoRepository {

     Set<BigInteger> findAllTimeTypeIdsByTimeTypeIds(List<BigInteger> timeTypeIds);
     Set<BigInteger> findTimeTypeIdssByTimeTypeEnum(List<String> timeTypeEnum);
     List<TimeTypeDTO> findTimeTypeWithItsParent();

}
