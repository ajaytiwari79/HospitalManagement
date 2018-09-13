package com.kairos.persistence.repository.wta;

import com.kairos.persistence.model.wta.StaffWTACounter;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * @author pradeep
 * @date - 23/5/18
 */
@Repository
public interface StaffWTACounterRepository extends MongoBaseRepository<StaffWTACounter,BigInteger> {

    @Query("{unitPositionId:?0,startDate:?1,endDate:?2,userHasStaffRole:?3}")
    List<StaffWTACounter> getStaffWTACounterByDate(Long unitPositionId,Date startDate,Date endDate,boolean userHasStaffRole);

}
