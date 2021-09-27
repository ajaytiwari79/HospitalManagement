package com.kairos.persistence.repository.shift;

import com.kairos.persistence.model.shift.BlockSetting;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

/**
 * Created By Pavan on 13/06/21
 **/
@Repository
public interface BlockSettingMongoRepository extends MongoBaseRepository<BlockSetting, BigInteger> {

    @Query("{unitId:?0,deleted:false,date:?1}")
    BlockSetting findBlockSettingByUnitIdAndDate(Long unitId, LocalDate date);

    @Query("{unitId:?0,deleted:false,date:{$gte:?1,$lte:?2}}")
    List<BlockSetting> findAllBlockSettingByUnitIdAndDateRange(Long unitId, LocalDate startDate, LocalDate endDate);
}
