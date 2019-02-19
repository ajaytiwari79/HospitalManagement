package com.kairos.persistence.repository.pay_out;

import com.kairos.persistence.model.pay_out.PayOutPerShift;

import java.util.Date;
import java.util.List;

/**
 * @author pradeep
 * @date - 24/7/18
 */

public interface CustomPayOutRepository {

    PayOutPerShift findLastPayoutByUnitPositionId(Long unitPositionId, Date date);

    List<PayOutPerShift> findAllLastPayoutByUnitPositionIds(List<Long> unitPositionId, Date startDate);

    void updatePayOut(Long unitPositionId,int payOut);
}
