package com.kairos.dto.activity.counter.distribution.dashboard;

import com.kairos.dto.activity.counter.configuration.KPIDTO;
import com.kairos.dto.activity.counter.distribution.tab.KPIPosition;

import java.math.BigInteger;

public class DashboardKPIDTO {
    private BigInteger id;
    private String tabId;
    private KPIDTO kpi;
    private String data;
    private KPIPosition position;
}
