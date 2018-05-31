package com.kairos.activity.persistence.repository.wta;

import com.kairos.activity.persistence.model.wta.StaffWTACounter;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
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

    @Query("{startDate:?0,endDate:?1,phaseName:?2}")
    List<StaffWTACounter> getStaffWTACounterByDate(Date startDate,Date endDate,String phaseName);

}
