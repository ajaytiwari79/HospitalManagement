package com.kairos.persistence.repository.pay_out;

import com.kairos.persistence.model.pay_out.PayOutPerShift;

import java.util.Date;
import java.util.List;

/**
 * @author pradeep
 * @date - 24/7/18
 */

public interface CustomPayOutRepository {

    PayOutPerShift findLastPayoutByEmploymentId(Long unitPositionId, Date date);

    void updatePayOut(Long unitPositionId,int payOut);
}
