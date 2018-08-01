package com.kairos.persistence.repository.pay_out;

import com.kairos.persistence.model.pay_out.PayOut;

import java.util.Date;

/**
 * @author pradeep
 * @date - 24/7/18
 */

public interface CustomPayOutRepository {

    PayOut findLastPayoutByUnitPositionId(Long unitPositionId, Date date);

    void updatePayOut(Long unitPositionId,int payOut);
}
