package com.kairos.persistence.repository.pay_out;

import com.kairos.persistence.model.pay_out.PayOutPerShift;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Set;

/*
* Created By Pradeep singh rajawat
*  Date-27/01/2018
*
* */

@Repository
public interface PayOutRepository extends MongoBaseRepository<PayOutPerShift,BigInteger>,CustomPayOutRepository {

    @Query("{employmentId:{$in:?0},date:{$gte:?1 , $lte:?2},deleted:false}")
    List<PayOutPerShift> findAllByEmploymentsAndDate(Set<Long> employmentIds, Date startDate, Date endDate);

    @Query("{employmentId:?0,date:{$gte:?1 , $lte:?2},deleted:false}")
    List<PayOutPerShift> findAllByEmploymentAndDate(Long employmentId, Date startDate, Date endDate);
    
    @Query("{employmentId:?0,date:{$lt:?1},deleted:false}")
    List<PayOutPerShift> findAllByEmploymentAndBeforeDate(Long employmentId, Date payOutDate);

    @Query("{shiftId:?0,deleted:false}")
    PayOutPerShift findAllByShiftId(BigInteger shiftId);

    @Query("{employmentId:{$in:?0},date:{$gte:?1 , $lte:?2},shiftId:?3,deleted:false}")
    List<PayOutPerShift> findByEmploymentsAndDateShiftId(Set<Long> employmentIds,Date startDate, Date endDate,BigInteger shiftId);
}
