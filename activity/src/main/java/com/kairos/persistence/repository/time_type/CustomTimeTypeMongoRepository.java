package com.kairos.persistence.repository.time_type;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

public interface CustomTimeTypeMongoRepository {

     Set<BigInteger> findAllTimeTypeIdsByTimeTypeIds(List<BigInteger> timeTypeIds);
     Set<BigInteger> findTimeTypeIdssByTimeTypeEnum(List<String> timeTypeEnum);

}
