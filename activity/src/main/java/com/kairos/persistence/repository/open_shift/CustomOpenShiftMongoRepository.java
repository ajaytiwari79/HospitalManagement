package com.kairos.persistence.repository.open_shift;

import com.kairos.persistence.model.open_shift.OpenShift;
import com.kairos.persistence.model.open_shift.OpenShiftActivityWrapper;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * Created by vipul on 14/5/18.
 */
public interface CustomOpenShiftMongoRepository {

    List<OpenShift> getOpenShiftsByUnitIdAndDate(Long unitId, Date startDate, Date endDate);

    OpenShiftActivityWrapper getOpenShiftAndActivity(BigInteger openShiftId, Long unitId);

}
