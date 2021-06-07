package com.kairos.service.shift;

import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Document
public class ActivityCardInformation extends MongoBaseEntity {
    private boolean countryAdminSetting;
    private Long unitId;
    private Long staffId;
    private Set<ActivityCardInformationSetting> activityCardInformationSettings;



    enum ActivityCardInformationSetting{
        STATUS,PRIORITY,ESCALATION,CHILD,BREAK_TIME,RESTING_HOURS
    }
}
