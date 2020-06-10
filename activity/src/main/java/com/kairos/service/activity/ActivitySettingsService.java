package com.kairos.service.activity;

import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.wrapper.activity.ActivityTimeTypeWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class ActivitySettingsService {

    @Inject
    private ActivityMongoRepository activityMongoRepository;

    public void updateTimeTypePathInActivity(final Activity activity) {
        List<ActivityTimeTypeWrapper> activityTimeTypeWrappers = activityMongoRepository.getActivityPath(activity.getId().toString());
        if (CollectionUtils.isNotEmpty(activityTimeTypeWrappers)) {
            ActivityTimeTypeWrapper activityTimeTypeWrapper = activityTimeTypeWrappers.get(0);
            String rootTimeType = activityTimeTypeWrapper.getTimeTypeHierarchyList().size() > 0 ? activityTimeTypeWrapper.getTimeTypeHierarchyList().get(0).getTimeTypes() : "";
            final StringBuilder path = new StringBuilder(",");
            path.append(rootTimeType).append(",");
            activityTimeTypeWrapper.getTimeTypeHierarchyList().forEach(timeTypeHierarchy -> {
                path.append(timeTypeHierarchy.getId()).append(",");
            });
            activity.setPath(path.toString());
        }
    }

}
