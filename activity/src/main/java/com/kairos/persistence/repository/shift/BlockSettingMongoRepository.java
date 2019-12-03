package com.kairos.persistence.repository.shift;

import com.kairos.persistence.model.shift.BlockSetting;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;

/**
 * Created By G.P.Ranjan on 3/12/19
 **/
@Repository
public interface BlockSettingMongoRepository extends MongoBaseRepository<BlockSetting, BigInteger> {

    @Query("{unitId:?0,deleted:false,date:?1}")
    BlockSetting findBlockSettingByUnitIdAndDate(Long unitId, LocalDate date);
}
