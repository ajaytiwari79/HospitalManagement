package com.kairos.service.shift;

import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.*;

@Service
public class ShiftHelperService {

    @Inject
    private ShiftMongoRepository shiftMongoRepository;

    @Async
    public void updateBackgroundColorInActivityAndShift(Activity activity, TimeType timeType) {
        List<Shift> shifts = shiftMongoRepository.findShiftByShiftActivityIdAndBetweenDate(newArrayList(activity.getId()), null, null, null);
        updateShiftActivityBackGroundColor(activity, timeType, shifts);
        if (isCollectionNotEmpty(shifts)) {
            shiftMongoRepository.saveEntities(shifts);
        }
    }

    private void updateShiftActivityBackGroundColor(Activity activity, TimeType timeType, List<Shift> shifts) {
        shifts.forEach(shift -> shift.getActivities().forEach(shiftActivity -> {
            if (shiftActivity.getActivityId().equals(activity.getId())) {
                if (isNotNull(timeType)) {
                    shiftActivity.setBackgroundColor(timeType.getBackgroundColor());
                    shiftActivity.setSecondLevelTimeType(timeType.getSecondLevelType());
                }
                shiftActivity.setUltraShortName(activity.getActivityGeneralSettings().getUltraShortName());
                shiftActivity.setShortName(activity.getActivityGeneralSettings().getShortName());
            }
            shiftActivity.getChildActivities().forEach(childActivity -> {
                if (childActivity.getActivityId().equals(activity.getId())) {
                    if (isNotNull(timeType)) {
                        childActivity.setBackgroundColor(timeType.getBackgroundColor());
                        childActivity.setSecondLevelTimeType(timeType.getSecondLevelType());
                    }
                    shiftActivity.setUltraShortName(activity.getActivityGeneralSettings().getUltraShortName());
                    shiftActivity.setShortName(activity.getActivityGeneralSettings().getShortName());
                }
            });
        }));
    }
}
