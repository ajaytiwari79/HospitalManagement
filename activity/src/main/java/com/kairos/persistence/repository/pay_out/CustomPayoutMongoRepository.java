package com.kairos.persistence.repository.pay_out;

import com.kairos.persistence.model.pay_out.PayOut;

/**
 * @author pradeep
 * @date - 24/7/18
 */

public interface CustomPayoutMongoRepository {

    PayOut findLastPayoutByUnitPositionId(Long unitPositionId);
}
