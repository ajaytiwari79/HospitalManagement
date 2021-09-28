package com.kairos.persistence.repository.pay_out;

import com.kairos.persistence.model.pay_out.PayOutPerShift;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author pradeep
 * @date - 24/7/18
 */

public interface CustomPayOutRepository {

    PayOutPerShift findLastPayoutByEmploymentId(Long employmentId, Date date);

    void updatePayOut(Long employmentId,int payOut);

    List<PayOutPerShift> findAllByEmploymentAndBeforeDate(Set<LocalDate> dateSet, Set<DayOfWeek> dayOfWeekSet,Long employmentId, Date payOutDate);

    List<PayOutPerShift> findAllByEmploymentsAndDate(Set<LocalDate> dateSet, Set<DayOfWeek> dayOfWeekSet,Collection<Long> employmentIds, Date startDate, Date endDate);
}
