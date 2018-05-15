package com.kairos.activity.persistence.repository.open_shift;

import com.kairos.response.dto.web.open_shift.OpenShiftResponseDTO;

import java.util.Date;
import java.util.List;

/**
 * Created by vipul on 14/5/18.
 */
public interface CustomOpenShiftMongoRepository {
    List<OpenShiftResponseDTO> getOpenShiftsByUnitIdAndSelectedDate(Long unitId, Date selectedDate);

}
