package com.kairos.activity.persistence.repository.open_shift;

import com.kairos.activity.persistence.model.open_shift.OpenShift;
import com.kairos.activity.persistence.model.open_shift.OpenShiftAndActivityWrapper;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * Created by vipul on 14/5/18.
 */
public interface CustomOpenShiftMongoRepository {

    List<OpenShift> getOpenShiftsByUnitIdAndDate(Long unitId, Date startDate, Date endDate);

    OpenShiftAndActivityWrapper getOpenShiftAndActivity(BigInteger openShiftId,Long unitId);

}
