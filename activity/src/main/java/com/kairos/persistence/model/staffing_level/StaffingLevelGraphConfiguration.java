package com.kairos.persistence.model.staffing_level;

import com.kairos.dto.activity.staffing_level.DailyGraphConfiguration;
import com.kairos.dto.activity.staffing_level.WeeklyGraphConfiguration;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document
public class StaffingLevelGraphConfiguration extends MongoBaseEntity {
    private static final long serialVersionUID = 1066350833651504944L;
    private WeeklyGraphConfiguration weeklyGraphConfiguration;
    private DailyGraphConfiguration dailyGraphConfiguration;
    private Long userId;
    private Long unitId;
}
