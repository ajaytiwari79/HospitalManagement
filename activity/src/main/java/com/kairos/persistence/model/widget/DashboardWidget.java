package com.kairos.persistence.model.widget;

import com.kairos.enums.widget.WidgetFilterType;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Set;

/**
 * pradeep
 * 29/4/19
 */
@Setter
@Getter
public class DashboardWidget extends MongoBaseEntity {

    private Set<BigInteger> timeTypeIds;
    private Set<WidgetFilterType> widgetFilterTypes;
    private Long userId;

    public DashboardWidget(Set<BigInteger> timeTypeIds, Set<WidgetFilterType> widgetFilterTypes) {
        this.timeTypeIds = timeTypeIds;
        this.widgetFilterTypes = widgetFilterTypes;
    }
}
